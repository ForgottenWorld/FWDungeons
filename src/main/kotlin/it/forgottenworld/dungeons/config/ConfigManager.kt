package it.forgottenworld.dungeons.config

import it.forgottenworld.dungeons.FWDungeonsPlugin
import it.forgottenworld.dungeons.model.dungeon.FinalDungeon
import it.forgottenworld.dungeons.model.instance.DungeonFinalInstance
import it.forgottenworld.dungeons.utils.ktx.getPlugin
import it.forgottenworld.dungeons.utils.ktx.launchAsync
import org.bukkit.Bukkit
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File


object ConfigManager {

    lateinit var config: FileConfiguration

    val isInDebugMode by lazy { config.getBoolean("debugMode", false) }

    private val dungeonWorldId by lazy {
        config.getString("dungeonWorld")
                ?.let { Bukkit.getWorld(it)?.uid ?: throw Exception("Dungeon world not found!") }
                ?: throw Exception("dungeonWorld missing from config!")
    }

    val easyRankingIntegration by lazy { config.getBoolean("easyRankingIntegration", false) }
    var useEasyRanking = false

    val dungeonWorld
        get() = Bukkit.getWorld(dungeonWorldId) ?: throw Exception("Dungeon world not found!")

    private fun loadConfig(config: FileConfiguration) {
        this.config = config
    }

    private val dungeonNameRegex = Regex("""[0-9]+\.yml""")
    private fun loadDungeonConfigs(dataFolder: File) {
        val dir = File(dataFolder, "dungeons").apply { if (isFile || (!exists() && mkdir())) return }
        dir.list()
                ?.filter { it.matches(dungeonNameRegex) }
                ?.forEach {
                    try {
                        val dId = it.removeSuffix(".yml").toInt()
                        val conf = YamlConfiguration().apply { load(File(dir, it)) }
                        FinalDungeon.dungeons[dId] = FinalDungeon.fromConfig(dId, conf)
                    } catch (e : Exception) {
                        e.printStackTrace()
                    }
                }
    }

    fun saveDungeonConfig(dataFolder: File, dungeon: FinalDungeon, eraseEffects: Boolean = false) {
        try {
            val dir = File(dataFolder, "dungeons").apply { if (!exists() && !mkdir()) return }
            val file = File(dir, "${dungeon.id}.yml")

            val existsAlready = file.exists()
            if (!existsAlready && !file.createNewFile()) return

            val conf = YamlConfiguration()
            if (existsAlready) conf.load(file)

            dungeon.toConfig(conf, eraseEffects)
            @Suppress("BlockingMethodInNonBlockingContext")
            launchAsync { conf.save(file) }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getInstancesFromConfig() {
        val file = File(FWDungeonsPlugin.pluginDataFolder, "instances.yml")
        val conf = YamlConfiguration()
        if (file.exists()) conf.load(file) else file.createNewFile()

        for (dId in FinalDungeon.dungeons.keys) {
            val sec = conf.getConfigurationSection("$dId")
            if (sec?.getKeys(false)?.isEmpty() != false) {
                Bukkit.getLogger().warning("Dungeon $dId has no instances, create one with /fwde d import $dId")
                FinalDungeon.dungeons[dId]?.isActive = false
                continue
            }
            for (iId in sec.getKeys(false)) {
                DungeonFinalInstance.fromConfig(dId, sec.getConfigurationSection(iId)!!)
            }
        }
    }

    fun loadData() {
        getPlugin().reloadConfig()
        loadConfig(FWDungeonsPlugin.pluginConfig)
        loadDungeonConfigs(FWDungeonsPlugin.pluginDataFolder)
        getInstancesFromConfig()
    }
}