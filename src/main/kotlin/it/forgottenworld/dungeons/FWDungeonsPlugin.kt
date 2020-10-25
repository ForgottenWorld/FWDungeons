package it.forgottenworld.dungeons

import it.forgottenworld.dungeons.command.FWDungeonsCommand
import it.forgottenworld.dungeons.command.FWDungeonsEditCommand
import it.forgottenworld.dungeons.config.ConfigManager
import it.forgottenworld.dungeons.event.listener.EntityDeathListener
import it.forgottenworld.dungeons.event.listener.PlayerListener
import it.forgottenworld.dungeons.task.TriggerChecker
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

        logger.info("Loading data...")

        ConfigManager.loadData()

        logger.info("Registering commands...")

        getCommand("fwdungeonsedit")?.setExecutor(FWDungeonsEditCommand())
        getCommand("fwdungeons")?.setExecutor(FWDungeonsCommand())

        logger.info("Registering events...")

        server.pluginManager.registerEvents(EntityDeathListener(), this)
        server.pluginManager.registerEvents(PlayerListener(), this)

        logger.info("Registering trigger checker...")

        TriggerChecker.start()
    }

    override fun onDisable() {
        logger.info("Disabling FWDungeons...")
    }

}