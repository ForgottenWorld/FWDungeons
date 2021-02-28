package it.forgottenworld.dungeons.core.config

import it.forgottenworld.dungeons.core.FWDungeonsPlugin
import it.forgottenworld.dungeons.core.config.Storage.toConfig
import it.forgottenworld.dungeons.core.game.dungeon.DungeonManager
import it.forgottenworld.dungeons.core.game.dungeon.FinalDungeon
import it.forgottenworld.dungeons.core.game.instance.DungeonInstanceImpl
import it.forgottenworld.dungeons.core.utils.launchAsync
import org.bukkit.Bukkit
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import javax.naming.ConfigurationException


object Configuration {

    lateinit var config: FileConfiguration

    val isDebugMode by lazy { config.getBoolean("debugMode", false) }

    private val dungeonWorldId by lazy {
        config.getString("dungeonWorld")
            ?.let { Bukkit.getWorld(it)?.uid ?: error("Dungeon world not found!") }
            ?: throw ConfigurationException("dungeonWorld missing from config!")
    }

    val easyRankingIntegration by lazy { config.getBoolean("easyRankingIntegration", false) }
    var useEasyRanking = false

    val fwEchelonIntegration by lazy { config.getBoolean("fwEchelonIntegration") }
    var useFWEchelon = false

    val vaultIntegration by lazy { config.getBoolean("vaultIntegration") }
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
                val dungeon = Storage.load<FinalDungeon>(config)
                DungeonManager.finalDungeons[dungeon.id] = dungeon
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    fun saveDungeonConfig(dungeon: FinalDungeon) {
        try {
            val dir = File(
                FWDungeonsPlugin.getInstance().dataFolder,
                "dungeons"
            ).apply {
                if (!exists() && !mkdir()) return
            }
            val file = File(dir, "${dungeon.id}.yml")

            val existsAlready = file.exists()
            if (!existsAlready && !file.createNewFile()) return

            val conf = YamlConfiguration()
            if (existsAlready) conf.load(file)

            dungeon.toConfig(conf)
            launchAsync { conf.save(file) }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadInstancesFromConfig() {
        val file = File(FWDungeonsPlugin.getInstance().dataFolder, "instances.yml")
        val conf = YamlConfiguration()
        if (file.exists()) conf.load(file) else file.createNewFile()

        for (dId in DungeonManager.finalDungeons.keys) {
            val sec = conf.getConfigurationSection("$dId")!!
            if (sec.getKeys(false).isEmpty()) {
                Bukkit.getLogger().warning(
                    "Dungeon $dId loaded from config has no instances, create one with /fwde d import $dId"
                )
                DungeonManager.finalDungeons[dId]?.isActive = false
                continue
            }
            for (iId in sec.getKeys(false)) {
                DungeonInstanceImpl.fromConfig(dId, sec.getConfigurationSection(iId)!!)
            }
        }
    }

    fun loadData() {
        FWDungeonsPlugin.getInstance().reloadConfig()
        FWDungeonsPlugin.getInstance().loadStrings()
        config = FWDungeonsPlugin.getInstance().config
        loadDungeonConfigs(FWDungeonsPlugin.getInstance().dataFolder)
        loadInstancesFromConfig()
    }
}