package it.forgottenworld.dungeons

import it.forgottenworld.dungeons.command.CommandFWDungeons
import it.forgottenworld.dungeons.command.CommandFWDungeonsEdit
import it.forgottenworld.dungeons.config.ConfigManager
import it.forgottenworld.dungeons.db.DBHandler
import it.forgottenworld.dungeons.db.executeUpdate
import it.forgottenworld.dungeons.event.listener.TriggerListener
import org.bukkit.plugin.java.JavaPlugin

var pluginInstance : FWDungeonsPlugin? = null

class FWDungeonsPlugin : JavaPlugin() {

    override fun onEnable() {
        logger.info("Enabling FWDungeons...")

        pluginInstance = this
        ConfigManager.loadConfig(config)

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
        pluginInstance = null
    }

    private fun initTables() {
            executeUpdate("CREATE TABLE IF NOT EXISTS fwdungeons.fwd_dungeons (\n" +
                            "  id INT NOT NULL AUTO_INCREMENT,\n" +
                            "  name VARCHAR(60) NOT NULL,\n" +
                            "  PRIMARY KEY (id),\n" +
                            "  UNIQUE INDEX name_UNIQUE (name ASC) VISIBLE);\n")
    }

}