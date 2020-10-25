package it.forgottenworld.dungeons.config

import it.forgottenworld.dungeons.FWDungeonsPlugin
import it.forgottenworld.dungeons.manager.DungeonManager
import it.forgottenworld.dungeons.model.Dungeon
import it.forgottenworld.dungeons.model.DungeonInstance
import it.forgottenworld.dungeons.utils.bukkitThreadAsync
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File


object ConfigManager {

    lateinit var config: FileConfiguration

    val isInDebugMode: Boolean by lazy { config.getBoolean("debugMode") }
    val dungeonWorld: String by lazy { config.getString("dungeonWorld")!! }

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
                        DungeonManager.dungeons[conf.getInt("id")] = Dungeon.fromConfig(conf)
                    } catch (e : Exception) {
                        e.printStackTrace()
                    }
                }
    }

    fun saveDungeonConfig(dataFolder: File, dungeon: Dungeon, eraseEffects: Boolean = false) {
        try {
            val dir = File(dataFolder, "dungeons").apply { if (!exists() && !mkdir()) return }
            val file = File(dir, "${dungeon.id}.yml").apply { if (!exists() && !createNewFile()) return }
            val conf = YamlConfiguration().apply { load(file) }
            dungeon.toConfig(conf, eraseEffects)
            bukkitThreadAsync { conf.save(file) }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getInstancesFromConfig() {
        val file = File(FWDungeonsPlugin.pluginDataFolder, "instances.yml")
        YamlConfiguration().run {
            if (file.exists()) load(file) else file.createNewFile()
            getKeys(false).forEach { k ->
                getConfigurationSection(k)
                        ?.let { DungeonInstance.fromConfig(it) }
                        ?.let { it.dungeon.instances.add(it) }
            }
        }
    }

    fun loadData() {
        loadConfig(FWDungeonsPlugin.pluginConfig)
        loadDungeonConfigs(FWDungeonsPlugin.pluginDataFolder)
        getInstancesFromConfig()
    }
}