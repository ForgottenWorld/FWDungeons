package it.forgottenworld.dungeons

import it.forgottenworld.dungeons.command.CommandFWDungeons
import it.forgottenworld.dungeons.command.CommandFWDungeonsEdit
import it.forgottenworld.dungeons.config.ConfigManager
import it.forgottenworld.dungeons.controller.FWDungeonsController
import it.forgottenworld.dungeons.db.DBHandler
import it.forgottenworld.dungeons.db.executeQuery
import it.forgottenworld.dungeons.db.executeUpdate
import it.forgottenworld.dungeons.event.listener.TriggerListener
import it.forgottenworld.dungeons.model.activearea.ActiveArea
import it.forgottenworld.dungeons.model.dungeon.DungeonInstance
import it.forgottenworld.dungeons.model.trigger.Trigger
import it.forgottenworld.dungeons.utils.getBlockVector
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.util.BlockVector
import sun.security.krb5.Config
import java.io.File

class FWDungeonsPlugin : JavaPlugin() {
    companion object {
        lateinit var instance: FWDungeonsPlugin
        lateinit var dataFolder: File
    }

    override fun onEnable() {
        logger.info("Enabling FWDungeons...")

        instance = this
        FWDungeonsPlugin.dataFolder = dataFolder
        saveDefaultConfig()
        ConfigManager.loadConfig(config)
        ConfigManager.loadDungeonConfigs(dataFolder)

        logger.info("Connecting to DB...")

        DBHandler.connect(
                config.getString("dbHost")!!,
                config.getString("dbDatabase")!!,
                config.getString("dbUsername")!!,
                config.getString("dbPassword")!!,
                config.getInt("dbPort"))

        logger.info("Checking and creating tables...")

        initTables()

        logger.info("Retrieving instance locations from DB...")

        getInstancesFromDB()

        logger.info("Registering commands...")

        getCommand("fwdungeonsedit")?.setExecutor(
                CommandFWDungeonsEdit()
        )
        getCommand("fwdungeons")?.setExecutor(
                CommandFWDungeons()
        )

        logger.info("Registering events...")

        server.pluginManager.registerEvents(TriggerListener(), this)
    }

    override fun onDisable() {
        logger.info("Disabling FWDungeons...")
    }

    private fun initTables() {
        executeUpdate("CREATE TABLE fwd_instance_locations (\n" +
                "id int NOT NULL AUTO_INCREMENT,\n" +
                "dungeon_id int NOT NULL,\n" +
                "instance_id int NOT NULL,\n" +
                "x int NOT NULL,\n" +
                "y int NOT NULL,\n" +
                "z int NOT NULL,\n" +
                "PRIMARY KEY (id),\n" +
                "UNIQUE KEY uqIdDungeonIdInstance (instance_id,dungeon_id));")
    }

    private fun getInstancesFromDB() {
        executeQuery("SELECT * FROM fwd_instance_locations;") { res ->
            while (res.next()) {
                val dungeon = FWDungeonsController.dungeons[res.getInt("dungeon_id")]
                val instOrigin = BlockVector(
                        res.getInt("x"),
                        res.getInt("y"),
                        res.getInt("z"))
                dungeon?.instances?.add(
                        DungeonInstance(
                                res.getInt("instance_id"),
                                dungeon,
                                instOrigin,
                                dungeon.triggers.map {
                                    Trigger(it.id,
                                            dungeon,
                                            it.box.withContainerOrigin(BlockVector(0,0,0), instOrigin),
                                            it.effect,
                                            it.requiresWholeParty
                                    )
                                },
                                dungeon.activeAreas.map {
                                    ActiveArea(it.id,
                                            it.box.withContainerOrigin(BlockVector(0,0,0), instOrigin)
                                    )
                                }
                        )
                )
            }
        }
    }

}