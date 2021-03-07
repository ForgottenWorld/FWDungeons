package it.forgottenworld.dungeons.core.config

import com.google.inject.Inject
import com.google.inject.Singleton
import it.forgottenworld.dungeons.api.game.dungeon.Dungeon
import it.forgottenworld.dungeons.api.game.dungeon.FinalDungeon
import it.forgottenworld.dungeons.api.game.dungeon.instance.DungeonInstance
import it.forgottenworld.dungeons.api.storage.Storage
import it.forgottenworld.dungeons.api.storage.Storage.Companion.load
import it.forgottenworld.dungeons.core.FWDungeonsPlugin
import it.forgottenworld.dungeons.core.game.dungeon.DungeonManager
import it.forgottenworld.dungeons.api.serialization.forEachSection
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

    private fun getDungeonsDir(): File {
        val dir = File(plugin.dataFolder, "dungeons")
        if (!dir.exists()) dir.mkdir()
        return dir
    }

    private fun loadDungeonConfigs() {
        val dir = getDungeonsDir()
        for (file in dir.list()?.filter { it.matches(dungeonNameRegex) } ?: return) {
            try {
                val config = YamlConfiguration().apply { load(File(dir, file)) }
                val dungeon = storage.load<Dungeon>(config)
                dungeonManager.registerFinalDungeon(dungeon as FinalDungeon)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    fun saveDungeonConfig(dungeon: FinalDungeon) {
        try {
            val dir = getDungeonsDir()
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
        if (file.exists()) {
            conf.load(file)
        } else {
            file.createNewFile()
        }

        for (dungeonId in dungeonManager.finalDungeonIds) {
            val sec = conf.getConfigurationSection("$dungeonId")
                ?: conf.createSection("$dungeonId")
            var any = false
            sec.forEachSection { _, section ->
                any = true
                storage.load<DungeonInstance>(section)
            }
            if (!any) {
                sendConsoleMessage(
                    "${Strings.CONSOLE_PREFIX}Dungeon $dungeonId loaded " +
                        "from config has no instances, create one with " +
                        "/fwde d import $dungeonId"
                )
                dungeonManager.disableDungeon(dungeonId)
                continue
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
        loadDungeonConfigs()

        sendConsoleMessage(" -- Loading instances...")
        loadInstancesFromConfig()
    }
}