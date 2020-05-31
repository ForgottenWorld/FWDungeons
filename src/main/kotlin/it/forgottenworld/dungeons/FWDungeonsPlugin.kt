package it.forgottenworld.dungeons

import it.forgottenworld.dungeons.db.DBHandler
import it.forgottenworld.dungeons.events.listener.TriggerListener
import org.bukkit.plugin.java.JavaPlugin

class FWDungeonsPlugin : JavaPlugin() {

    override fun onEnable() {
        logger.info("Enabling FWDungeons...")
        logger.info("Connecting to DB...")
        DBHandler.connect(
                config.getString("dbHost")!!,
                config.getString("dbDatabase")!!,
                config.getString("dbUsername")!!,
                config.getString("dbPassword")!!,
                config.getInt("dbPort"))
        logger.info("Registering events...")
        server.pluginManager.registerEvents(TriggerListener(), this)

    }

    override fun onDisable() {
        logger.info("Disabling FWDungeons...")
    }

}