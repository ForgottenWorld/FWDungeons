package it.forgottenworld.dungeons

import it.forgottenworld.dungeons.command.CommandFWDungeons
import it.forgottenworld.dungeons.command.CommandFWDungeonsEdit
import it.forgottenworld.dungeons.config.ConfigManager
import it.forgottenworld.dungeons.db.DBHandler
import it.forgottenworld.dungeons.db.executeUpdate
import it.forgottenworld.dungeons.event.listener.TriggerListener
import org.bukkit.plugin.java.JavaPlugin
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
            executeUpdate("CREATE TABLE IF NOT EXISTS fwdungeons.fwd_instance_locations (\n" +
                            "  id INT NOT NULL AUTO_INCREMENT,\n" +
                            "  dungeon_id INT NOT NULL,\n" +
                            "  x INT NOT NULL,\n" +
                            "  y INT NOT NULL,\n" +
                            "  z INT NOT NULL,\n" +
                            "  PRIMARY KEY (id));")
    }

}