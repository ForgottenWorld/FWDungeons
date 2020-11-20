package it.forgottenworld.dungeons

import it.forgottenworld.dungeons.command.FWDungeonsCommand
import it.forgottenworld.dungeons.command.FWDungeonsEditCommand
import it.forgottenworld.dungeons.config.ConfigManager
import it.forgottenworld.dungeons.config.Strings
import it.forgottenworld.dungeons.event.listener.EntityDeathListener
import it.forgottenworld.dungeons.event.listener.PlayerListener
import it.forgottenworld.dungeons.event.listener.RespawnHandler
import it.forgottenworld.dungeons.event.listener.TriggerActivationHandler
import it.forgottenworld.dungeons.utils.ktx.launch
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import org.bukkit.Bukkit
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.io.InputStreamReader

class FWDungeonsPlugin : JavaPlugin() {

    override fun onEnable() {
        logger.info("Enabling FWDungeons...")

        saveDefaultConfig()
        pluginDataFolder = dataFolder
        pluginConfig = config

        logger.info("Loading data...")

        loadStrings()
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

        val job = launch {
            logger.info("I will now start counting until the job is stopped")
            var i = 0
            while (true) {
                logger.info("${++i}")
                delay(1000)
            }
        }

        launch {
            delay(5000)
            job.cancel("It bloody fucking works")
        }
    }

    private fun loadStrings() {
        val stringsFile = File(dataFolder, "strings.yml")
        val conf = YamlConfiguration()
        if (!stringsFile.exists()) {
            YamlConfiguration().run {
                load(InputStreamReader(getResource("strings.it.yml")!!))
                save(File(dataFolder, "strings.it.yml"))
            }
            conf.load(InputStreamReader(getResource("strings.yml")!!))
            conf.save(stringsFile)
        } else conf.load(stringsFile)
        Strings.loadFromRes(conf)
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

        lateinit var pluginDataFolder: File
        lateinit var pluginConfig: FileConfiguration
    }

}