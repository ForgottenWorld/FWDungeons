package it.forgottenworld.dungeons

import it.forgottenworld.dungeons.command.CommandFWDungeons
import it.forgottenworld.dungeons.command.CommandFWDungeonsEdit
import it.forgottenworld.dungeons.db.DBHandler
import it.forgottenworld.dungeons.db.executeUpdate
import it.forgottenworld.dungeons.event.listener.EntityDeathListener
import it.forgottenworld.dungeons.event.listener.PlayerListener
import it.forgottenworld.dungeons.event.listener.TriggerListener
import it.forgottenworld.dungeons.state.loadData
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

class FWDungeonsPlugin : JavaPlugin() {
    companion object {
        lateinit var instance: FWDungeonsPlugin
        lateinit var pluginDataFolder: File
        lateinit var pluginConfig: FileConfiguration
    }

    override fun onEnable() {
        logger.info("Enabling FWDungeons...")

        instance = this
        saveDefaultConfig()
        pluginDataFolder = dataFolder
        pluginConfig = config

        logger.info("Connecting to DB...")

        DBHandler.connect(
                config.getString("dbHost")!!,
                config.getString("dbDatabase")!!,
                config.getString("dbUsername")!!,
                config.getString("dbPassword")!!,
                config.getInt("dbPort"))

        logger.info("Checking and creating tables...")

        initTables()

        logger.info("Loading data...")

        loadData()

        logger.info("Registering commands...")

        getCommand("fwdungeonsedit")?.setExecutor(
                CommandFWDungeonsEdit()
        )
        getCommand("fwdungeons")?.setExecutor(
                CommandFWDungeons()
        )

        logger.info("Registering events...")

        server.pluginManager.registerEvents(TriggerListener(), this)
        server.pluginManager.registerEvents(EntityDeathListener(), this)
        server.pluginManager.registerEvents(PlayerListener(), this)
    }

    override fun onDisable() {
        logger.info("Disabling FWDungeons...")
    }

    private fun initTables() {
        executeUpdate("CREATE TABLE IF NOT EXISTS fwd_instance_locations ( " +
                "id int NOT NULL AUTO_INCREMENT, " +
                "dungeon_id int NOT NULL, " +
                "instance_id int NOT NULL, " +
                "x int NOT NULL, " +
                "y int NOT NULL, " +
                "z int NOT NULL, " +
                "PRIMARY KEY (id), " +
                "UNIQUE KEY uqIdDungeonIdInstance (instance_id,dungeon_id));")
    }

}