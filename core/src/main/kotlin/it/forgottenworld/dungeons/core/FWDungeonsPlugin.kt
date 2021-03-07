package it.forgottenworld.dungeons.core

import com.google.inject.Inject
import it.forgottenworld.dungeons.core.command.edit.FWDungeonsEditCommand
import it.forgottenworld.dungeons.core.command.play.FWDungeonsPlayCommand
import it.forgottenworld.dungeons.core.config.Configuration
import it.forgottenworld.dungeons.core.config.Strings
import it.forgottenworld.dungeons.core.game.dungeon.DungeonManager
import it.forgottenworld.dungeons.core.game.unlockables.UnlockableManager
import it.forgottenworld.dungeons.core.integrations.EasyRankingUtils
import it.forgottenworld.dungeons.core.integrations.FWEchelonUtils
import it.forgottenworld.dungeons.core.integrations.VaultUtils
import it.forgottenworld.dungeons.core.utils.sendConsoleMessage
import org.bukkit.plugin.java.JavaPlugin

class FWDungeonsPlugin : JavaPlugin() {

    @Inject
    private lateinit var vaultUtils: VaultUtils

    @Inject
    private lateinit var easyRankingUtils: EasyRankingUtils

    @Inject
    private lateinit var fwEchelonUtils: FWEchelonUtils

    @Inject
    private lateinit var fwDungeonsEditCommand: FWDungeonsEditCommand

    @Inject
    private lateinit var fWDungeonsPlayCommand: FWDungeonsPlayCommand

    @Inject
    private lateinit var configuration: Configuration

    @Inject
    private lateinit var bukkitEventListener: BukkitEventListener

    @Inject
    private lateinit var dungeonManager: DungeonManager

    @Inject
    private lateinit var unlockableManager: UnlockableManager

    fun loadData(reload: Boolean = true) {

        sendConsoleMessage("${Strings.CONSOLE_PREFIX}Loading configuration...")
        if (reload) {
            reloadConfig()
            configuration.reload()
        }

        sendConsoleMessage(" -- Loading strings...")
        Strings.load(this)

        sendConsoleMessage(" -- Loading unlockables...")
        unlockableManager.loadUnlockablesFromStorage()

        sendConsoleMessage(" -- Loading dungeons...")
        dungeonManager.loadDungeonsFromStorage()

        sendConsoleMessage(" -- Loading instances...")
        dungeonManager.loadInstancesFromStorage()
    }

    override fun onEnable() {

        sendConsoleMessage("${Strings.CONSOLE_PREFIX}Injecting dependencies...")

        DependenciesModule(this)
            .createInjector()
            .injectMembers(this)

        sendConsoleMessage("${Strings.CONSOLE_PREFIX}Saving default config...")

        saveDefaultConfig()

        loadData(false)

        sendConsoleMessage("${Strings.CONSOLE_PREFIX}Registering commands...")

        getCommand("fwdungeonsedit")!!.setExecutor(fwDungeonsEditCommand)
        getCommand("fwdungeons")!!.setExecutor(fWDungeonsPlayCommand)

        sendConsoleMessage("${Strings.CONSOLE_PREFIX}Registering events...")

        server.pluginManager.registerEvents(bukkitEventListener, this)

        easyRankingUtils.checkEasyRankingIntegration()
        fwEchelonUtils.checkFWEchelonIntegration()
        vaultUtils.checkVaultIntegration()
    }

    override fun onDisable() {
        sendConsoleMessage("Disabling FWDungeons...")
    }
}