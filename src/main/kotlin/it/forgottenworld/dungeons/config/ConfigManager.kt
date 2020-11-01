package it.forgottenworld.dungeons.config

import it.forgottenworld.dungeons.FWDungeonsPlugin
import it.forgottenworld.dungeons.model.dungeon.FinalDungeon
import it.forgottenworld.dungeons.model.instance.DungeonFinalInstance
import it.forgottenworld.dungeons.utils.launchAsync
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
            launchAsync { conf.save(file) }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getInstancesFromConfig() {
        val file = File(FWDungeonsPlugin.pluginDataFolder, "instances.yml")
        YamlConfiguration().run {
            if (file.exists()) load(file) else file.createNewFile()

            for (dId in getKeys(false)) {
                val sec = getConfigurationSection(dId) ?: continue
                val nDId = dId.toInt()
                for (iId in sec.getKeys(false)) {
                    DungeonFinalInstance.fromConfig(nDId, sec.getConfigurationSection(iId)!!)
                }
            }
        }
    }

    fun loadData() {
        FWDungeonsPlugin.instance.reloadConfig()
        loadConfig(FWDungeonsPlugin.pluginConfig)
        loadDungeonConfigs(FWDungeonsPlugin.pluginDataFolder)
        getInstancesFromConfig()
    }
}