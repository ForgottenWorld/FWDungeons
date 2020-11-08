package it.forgottenworld.dungeons

import it.forgottenworld.dungeons.command.FWDungeonsCommand
import it.forgottenworld.dungeons.command.FWDungeonsEditCommand
import it.forgottenworld.dungeons.config.ConfigManager
import it.forgottenworld.dungeons.event.listener.EntityDeathListener
import it.forgottenworld.dungeons.event.listener.PlayerListener
import it.forgottenworld.dungeons.event.listener.RespawnHandler
import it.forgottenworld.dungeons.event.listener.TriggerActivationHandler
import org.bukkit.Bukkit
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

class FWDungeonsPlugin : JavaPlugin() {

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
        server.pluginManager.registerEvents(TriggerActivationHandler(), this)
        server.pluginManager.registerEvents(RespawnHandler(), this)

        checkEasyRankingIntegration()
    }

    private fun checkEasyRankingIntegration() {
        logger.info("Checking for EasyRanking integration...")
        if (!ConfigManager.easyRankingIntegration) {
            logger.info("EasyRanking integration is not enabled")
            return
        }

        logger.info("EasyRanking integration is enabled")
        if (Bukkit.getPluginManager().getPlugin("Easyranking") == null) {
            logger.info("EasyRanking is not present")
            return
        }

        logger.info("EasyRanking is present")
        ConfigManager.useEasyRanking = true
    }

    override fun onDisable() {
        logger.info("Disabling FWDungeons...")
    }

    companion object {
        lateinit var instance: FWDungeonsPlugin
        lateinit var pluginDataFolder: File
        lateinit var pluginConfig: FileConfiguration
    }

}