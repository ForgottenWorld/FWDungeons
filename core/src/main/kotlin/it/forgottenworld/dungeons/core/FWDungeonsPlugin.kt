package it.forgottenworld.dungeons.core

import it.forgottenworld.dungeons.core.command.edit.FWDungeonsEditCommand
import it.forgottenworld.dungeons.core.command.play.FWDungeonsPlayCommand
import it.forgottenworld.dungeons.core.config.ConfigManager
import it.forgottenworld.dungeons.core.config.Strings
import it.forgottenworld.dungeons.core.game.objective.CombatObjective
import it.forgottenworld.dungeons.core.integrations.EasyRankingUtils
import it.forgottenworld.dungeons.core.integrations.FWEchelonUtils
import it.forgottenworld.dungeons.core.listener.BypassAttemptListener
import it.forgottenworld.dungeons.core.listener.PlayerListener
import it.forgottenworld.dungeons.core.listener.RespawnHandler
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
            RespawnHandler(),
            BypassAttemptListener()
        ).forEach { server.pluginManager.registerEvents(it, this) }

        EasyRankingUtils.checkEasyRankingIntegration()
        FWEchelonUtils.checkFWEchelonIntegration()
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


    override fun onDisable() {
        logger.info("Disabling FWDungeons...")
    }

}