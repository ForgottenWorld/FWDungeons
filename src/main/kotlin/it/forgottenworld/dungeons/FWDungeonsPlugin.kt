package it.forgottenworld.dungeons

import it.forgottenworld.dungeons.command.edit.FWDungeonsEditCommand
import it.forgottenworld.dungeons.command.play.FWDungeonsPlayCommand
import it.forgottenworld.dungeons.config.ConfigManager
import it.forgottenworld.dungeons.config.Strings
import it.forgottenworld.dungeons.game.objective.CombatObjective
import it.forgottenworld.dungeons.integrations.EasyRankingUtils
import it.forgottenworld.dungeons.integrations.FWEchelonUtils
import it.forgottenworld.dungeons.listener.BypassAttemptListener
import it.forgottenworld.dungeons.listener.PlayerListener
import it.forgottenworld.dungeons.listener.RespawnHandler
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