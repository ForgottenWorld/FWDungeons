package it.forgottenworld.dungeons

import it.forgottenworld.dungeons.command.play.FWDungeonsPlayCommand
import it.forgottenworld.dungeons.command.edit.FWDungeonsEditCommand
import it.forgottenworld.dungeons.config.ConfigManager
import it.forgottenworld.dungeons.config.Strings
import it.forgottenworld.dungeons.game.interactiveregion.Trigger
import it.forgottenworld.dungeons.game.objective.CombatObjective
import it.forgottenworld.dungeons.listener.BypassAttemptListener
import it.forgottenworld.dungeons.listener.PlayerListener
import it.forgottenworld.dungeons.listener.RespawnHandler
import org.bukkit.Bukkit
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.io.InputStreamReader

class FWDungeonsPlugin : JavaPlugin() {

    override fun onEnable() {
        saveDefaultConfig()

        logger.info("Loading data...")

        loadStrings()
        ConfigManager.loadData()

        logger.info("Registering commands...")

        getCommand("fwdungeonsedit")?.setExecutor(FWDungeonsEditCommand())
        getCommand("fwdungeons")?.setExecutor(FWDungeonsPlayCommand())

        logger.info("Registering events...")

        listOf(
            CombatObjective.EntityDeathListener(),
            PlayerListener(),
            Trigger.ActivationHandler(),
            RespawnHandler(),
            BypassAttemptListener()
        ).forEach { server.pluginManager.registerEvents(it, this) }

        checkEasyRankingIntegration()
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

}