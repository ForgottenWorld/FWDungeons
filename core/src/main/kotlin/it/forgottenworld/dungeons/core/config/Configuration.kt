package it.forgottenworld.dungeons.core.config

import com.google.inject.Inject
import com.google.inject.Singleton
import it.forgottenworld.dungeons.api.game.dungeon.Dungeon
import it.forgottenworld.dungeons.api.game.dungeon.FinalDungeon
import it.forgottenworld.dungeons.api.game.instance.DungeonInstance
import it.forgottenworld.dungeons.api.storage.Storage
import it.forgottenworld.dungeons.api.storage.Storage.Companion.load
import it.forgottenworld.dungeons.core.FWDungeonsPlugin
import it.forgottenworld.dungeons.core.game.DungeonManager
import it.forgottenworld.dungeons.core.utils.launchAsync
import it.forgottenworld.dungeons.core.utils.sendConsoleMessage
import org.bukkit.Bukkit
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import javax.naming.ConfigurationException

@Singleton
class Configuration @Inject constructor(
    private val plugin: FWDungeonsPlugin,
    private val storage: Storage,
    private val dungeonManager: DungeonManager
) {

    lateinit var config: FileConfiguration

    val isDebugMode by lazy { config.getBoolean("debugMode", false) }

    private val dungeonWorldId by lazy {
        config.getString("dungeonWorld")
            ?.let { Bukkit.getWorld(it)?.uid ?: error("Dungeon world not found!") }
            ?: throw ConfigurationException("dungeonWorld missing from config!")
    }

    val easyRankingIntegration by lazy { config.getBoolean("easyRankingIntegration", false) }
    var useEasyRanking = false

    val fwEchelonIntegration by lazy { config.getBoolean("fwEchelonIntegration", false) }
    var useFWEchelon = false

    val vaultIntegration by lazy { config.getBoolean("vaultIntegration", false) }
    var useVault = false

    val dungeonWorld
        get() = Bukkit.getWorld(dungeonWorldId) ?: error("Dungeon world not found!")

    private val dungeonNameRegex = Regex("""[0-9]+\.yml""")

    private fun loadDungeonConfigs(dataFolder: File) {
        val dir = File(dataFolder, "dungeons").apply {
            if (isFile || (!exists() && mkdir())) return
        }
        for (file in dir.list()?.filter { it.matches(dungeonNameRegex) } ?: return) {
            try {
                val config = YamlConfiguration().apply { load(File(dir, file)) }
                val dungeon = storage.load(Dungeon::class, config)
                dungeonManager.finalDungeons[dungeon.id] = dungeon as FinalDungeon
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    fun saveDungeonConfig(dungeon: FinalDungeon) {
        try {
            val dir = File(
                plugin.dataFolder,
                "dungeons"
            ).apply {
                if (!exists() && !mkdir()) return
            }
            val file = File(dir, "${dungeon.id}.yml")

            val existsAlready = file.exists()
            if (!existsAlready && !file.createNewFile()) return

            val conf = YamlConfiguration()
            if (existsAlready) conf.load(file)

            storage.save(dungeon, conf)
            launchAsync { conf.save(file) }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadInstancesFromConfig() {
        val file = File(plugin.dataFolder, "instances.yml")
        val conf = YamlConfiguration()
        if (file.exists()) conf.load(file) else file.createNewFile()

        for (dId in dungeonManager.finalDungeons.keys) {
            val sec = conf.getConfigurationSection("$dId")!!
            if (sec.getKeys(false).isEmpty()) {
                sendConsoleMessage(
                    "${Strings.CONSOLE_PREFIX}Dungeon $dId loaded from config has no instances, create one with /fwde d import $dId"
                )
                dungeonManager.finalDungeons[dId]?.isActive = false
                continue
            }
            for (iId in sec.getKeys(false)) {
                storage.load<DungeonInstance>(sec.getConfigurationSection(iId)!!)
            }
        }
    }

    fun loadData() {

        sendConsoleMessage("${Strings.CONSOLE_PREFIX}Loading configuration...")
        plugin.reloadConfig()

        sendConsoleMessage(" -- Loading strings...")
        Strings.load(plugin)
        config = plugin.config

        sendConsoleMessage(" -- Loading dungeons...")
        loadDungeonConfigs(plugin.dataFolder)

        sendConsoleMessage(" -- Loading instances...")
        loadInstancesFromConfig()
    }
}