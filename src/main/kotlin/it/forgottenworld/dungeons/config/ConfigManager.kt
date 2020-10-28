package it.forgottenworld.dungeons.config

import it.forgottenworld.dungeons.FWDungeonsPlugin
import it.forgottenworld.dungeons.manager.DungeonManager
import it.forgottenworld.dungeons.model.dungeon.FinalDungeon
import it.forgottenworld.dungeons.model.instance.DungeonFinalInstance
import it.forgottenworld.dungeons.utils.launchAsync
import org.bukkit.Bukkit
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File


object ConfigManager {

    lateinit var config: FileConfiguration

    val isInDebugMode: Boolean by lazy { config.getBoolean("debugMode") }

    private val dungeonWorldName: String by lazy {
        config.getString("dungeonWorld")
            ?: throw Exception("dungeonWorld missing from config!")
    }

    val dungeonWorld
        get() = Bukkit.getWorld(dungeonWorldName) ?: throw Exception("Dungeon world not found!")

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
                        val conf = YamlConfiguration().apply { load(File(dir, it)) }
                        DungeonManager.dungeons[conf.getInt("id")] = FinalDungeon.fromConfig(conf)
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
            getKeys(false)
                    .map { it to DungeonFinalInstance.fromConfig(getConfigurationSection(it)!!) }
        }
    }

    fun loadData() {
        FWDungeonsPlugin.instance.reloadConfig()
        loadConfig(FWDungeonsPlugin.pluginConfig)
        loadDungeonConfigs(FWDungeonsPlugin.pluginDataFolder)
        getInstancesFromConfig()
    }
}