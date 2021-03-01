package it.forgottenworld.dungeons.core

import com.google.inject.Inject
import it.forgottenworld.dungeons.core.command.edit.FWDungeonsEditCommand
import it.forgottenworld.dungeons.core.command.play.FWDungeonsPlayCommand
import it.forgottenworld.dungeons.core.config.Configuration
import it.forgottenworld.dungeons.core.integrations.EasyRankingUtils
import it.forgottenworld.dungeons.core.integrations.FWEchelonUtils
import it.forgottenworld.dungeons.core.integrations.VaultUtils
import org.bukkit.plugin.java.JavaPlugin

class FWDungeonsPlugin : JavaPlugin() {

    @Inject private lateinit var vaultUtils: VaultUtils
    @Inject private lateinit var easyRankingUtils: EasyRankingUtils
    @Inject private lateinit var fwEchelonUtils: FWEchelonUtils

    @Inject private lateinit var fwDungeonsEditCommand: FWDungeonsEditCommand
    @Inject private lateinit var fWDungeonsPlayCommand: FWDungeonsPlayCommand
    @Inject private lateinit var configuration: Configuration

    override fun onEnable() {

        logger.info("Injecting dependencies...")

        MainModule(this)
            .createInjector()
            .injectMembers(this)

        logger.info("Saving default config...")

        saveDefaultConfig()

        configuration.loadData()

        logger.info("Registering commands...")

        getCommand("fwdungeonsedit")!!.setExecutor(fwDungeonsEditCommand)
        getCommand("fwdungeons")!!.setExecutor(fWDungeonsPlayCommand)

        logger.info("Registering events...")

        server.pluginManager.registerEvents(SpigotEventDispatcher(), this)

        easyRankingUtils.checkEasyRankingIntegration()
        fwEchelonUtils.checkFWEchelonIntegration()
        vaultUtils.checkVaultIntegration()
    }

    override fun onDisable() {
        logger.info("Disabling FWDungeons...")
    }
}