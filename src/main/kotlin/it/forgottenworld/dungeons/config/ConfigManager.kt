package it.forgottenworld.dungeons.config

import it.forgottenworld.dungeons.controller.FWDungeonsController
import it.forgottenworld.dungeons.model.box.Box
import it.forgottenworld.dungeons.model.dungeon.Dungeon
import it.forgottenworld.dungeons.model.trigger.Trigger
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.util.BlockVector
import java.io.File

object ConfigManager {
    lateinit var config: FileConfiguration

    val isInDebugMode: Boolean by lazy { config.getBoolean("debugMode") }
    val dungeonWorld: String by lazy { config.getString("dungeonWorld")!! }

    fun loadConfig(config: FileConfiguration) {
        this.config = config
    }

    fun loadDungeonConfigs(dataFolder: File?) {
        val regex = Regex("""dungeon[0-9]+\.yml""")
        dataFolder?.list()?.filter {
            it.matches(regex)
        }?.forEach {
            try {
                val conf = YamlConfiguration().apply { load(it) }
                FWDungeonsController.dungeons[conf.getInt("id")] =
                        Dungeon(
                                conf.getInt("id"),
                                conf.getString("name")!!,
                                Box(
                                        BlockVector(0,0,0),
                                        conf.getInt("width"),
                                        conf.getInt("height"),
                                        conf.getInt("depth")
                                ),
                                mutableListOf(),
                                mutableListOf()
                        ).apply {
                            triggers.addAll(
                                    conf.getConfigurationSection("triggers")
                                    !!.getKeys(false)
                                            .map {k ->
                                                Trigger(
                                                        k.toInt(),
                                                        this,
                                                        Box(
                                                                conf.getVector("triggers.$k.origin")!!.toBlockVector(),
                                                                conf.getInt("triggers.$k.width"),
                                                                conf.getInt("triggers.$k.height"),
                                                                conf.getInt("triggers.$k.depth")
                                                        ),
                                                        { p ->
                                                            parseEffectFromConfig(p, conf.getString("triggers.$k.effect")!!)},
                                                        conf.getBoolean("triggers.$k.requiresWholeParty")
                                                )
                                            }
                            )
                        }
            } catch (e : Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun parseEffectFromConfig(player: Player, code: String) {

    }
}