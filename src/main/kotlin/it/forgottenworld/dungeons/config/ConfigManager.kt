package it.forgottenworld.dungeons.config

import it.forgottenworld.dungeons.controller.FWDungeonsController
import it.forgottenworld.dungeons.model.activearea.ActiveArea
import it.forgottenworld.dungeons.model.box.Box
import it.forgottenworld.dungeons.model.dungeon.Dungeon
import it.forgottenworld.dungeons.model.trigger.Trigger
import it.forgottenworld.dungeons.utils.toVector
import org.bukkit.configuration.InvalidConfigurationException
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.util.BlockVector
import java.io.File
import java.io.IOException


object ConfigManager {
    lateinit var config: FileConfiguration

    val isInDebugMode: Boolean by lazy { config.getBoolean("debugMode") }
    val dungeonWorld: String by lazy { config.getString("dungeonWorld")!! }

    fun loadConfig(config: FileConfiguration) {
        this.config = config
    }

    fun loadDungeonConfigs(dataFolder: File) {
        val regex = Regex("""[0-9]+\.yml""")
        val dir = File(dataFolder, "dungeons")
        if (!dir.isFile && !dir.exists()) dir.mkdir()
        dir.list()?.filter {
            it.matches(regex)
        }?.forEach {
            try {
                val conf = YamlConfiguration().apply { load(File(dir, it)) }
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
                                conf.getVector("startingLocation")!!.toBlockVector(),
                                mutableListOf(),
                                mutableListOf(),
                                mutableListOf()
                        ).apply {
                            triggers.addAll(
                                    conf.getConfigurationSection("triggers")
                                    !!.getKeys(false)
                                            .map { k ->
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
                            activeAreas.addAll(
                                    conf.getConfigurationSection("activeAreas")
                                    !!.getKeys(false)
                                            .map { k ->
                                                ActiveArea(
                                                        k.toInt(),
                                                        Box(
                                                                conf.getVector("activeAreas.$k.origin")!!.toBlockVector(),
                                                                conf.getInt("activeAreas.$k.width"),
                                                                conf.getInt("activeAreas.$k.height"),
                                                                conf.getInt("activeAreas.$k.depth")
                                                        )
                                                )
                                            }
                            )
                        }
            } catch (e : Exception) {
                e.printStackTrace()
            }
        }
    }

    fun saveDungeonConfig(dataFolder: File, dungeon: Dungeon, generateEmptyEffects: Boolean = false) {
        try {
            val dir = File(dataFolder, "dungeons")
            if (!dir.exists() && !dir.mkdir()) return
            val file = File(dir, "${dungeon.id}.yml")
            if (!file.exists()) file.createNewFile()

            YamlConfiguration().apply {
                load(file)
                set("id", dungeon.id)
                set("name", dungeon.name)
                set("width", dungeon.box.width)
                set("height", dungeon.box.height)
                set("depth", dungeon.box.depth)
                set("startingLocation", dungeon.startingLocation.toVector())
                dungeon.triggers.forEach {
                    set("triggers.${it.id}.id", it.id)
                    set("triggers.${it.id}.origin", it.origin.toVector())
                    set("triggers.${it.id}.width", it.box.width)
                    set("triggers.${it.id}.height", it.box.height)
                    set("triggers.${it.id}.depth", it.box.depth)
                    if (generateEmptyEffects)
                        set("triggers.${it.id}.effect", "")
                    set("triggers.${it.id}.requiresWholeParty", it.requiresWholeParty)
                }
                dungeon.activeAreas.forEach {
                    set("activeAreas.${it.id}.id", it.id)
                    set("activeAreas.${it.id}.origin", it.box.origin.toVector())
                    set("activeAreas.${it.id}.width", it.box.width)
                    set("activeAreas.${it.id}.height", it.box.height)
                    set("activeAreas.${it.id}.depth", it.box.depth)
                }
                save(file)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: InvalidConfigurationException) {
            e.printStackTrace()
        }
    }

    private fun parseEffectFromConfig(player: Player, code: String) {

    }
}