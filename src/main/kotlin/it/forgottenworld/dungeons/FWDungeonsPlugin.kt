package it.forgottenworld.dungeons

import it.forgottenworld.dungeons.db.DBHandler
import it.forgottenworld.dungeons.db.executeUpdate
import it.forgottenworld.dungeons.events.listener.TriggerListener
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class FWDungeonsPlugin : JavaPlugin() {

    val requiredTables = listOf(
            "fwd_dungeons"
    )

    override fun onEnable() {
        logger.info("Enabling FWDungeons...")

        logger.info("Connecting to DB...")

        DBHandler.connect(
                config.getString("dbHost")!!,
                config.getString("dbDatabase")!!,
                config.getString("dbUsername")!!,
                config.getString("dbPassword")!!,
                config.getInt("dbPort"))

        logger.info("Checking and creating tables...")

        initTables()

        logger.info("Registering events...")
        server.pluginManager.registerEvents(TriggerListener(), this)
    }

    override fun onDisable() {
        logger.info("Disabling FWDungeons...")
    }

    private fun initTables() {
            executeUpdate(this,
                    "CREATE TABLE IF NOT EXISTS fwdungeons.fwd_dungeons (\n" +
                            "  id INT NOT NULL AUTO_INCREMENT,\n" +
                            "  name VARCHAR(60) NOT NULL,\n" +
                            "  PRIMARY KEY (id),\n" +
                            "  UNIQUE INDEX name_UNIQUE (name ASC) VISIBLE);\n")
    }

}