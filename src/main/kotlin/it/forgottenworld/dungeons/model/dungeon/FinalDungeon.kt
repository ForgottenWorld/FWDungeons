package it.forgottenworld.dungeons.model.dungeon

import it.forgottenworld.dungeons.FWDungeonsPlugin
import it.forgottenworld.dungeons.config.ConfigManager
import it.forgottenworld.dungeons.config.Strings
import it.forgottenworld.dungeons.model.box.Box
import it.forgottenworld.dungeons.model.dungeon.EditableDungeon.Companion.editableDungeon
import it.forgottenworld.dungeons.model.instance.DungeonFinalInstance
import it.forgottenworld.dungeons.model.interactiveelement.ActiveArea
import it.forgottenworld.dungeons.model.interactiveelement.Trigger
import it.forgottenworld.dungeons.utils.ktx.blockVector
import it.forgottenworld.dungeons.utils.ktx.launchAsync
import it.forgottenworld.dungeons.utils.ktx.sendFWDMessage
import it.forgottenworld.dungeons.utils.ktx.toVector
import org.bukkit.block.Block
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.util.BlockVector
import java.io.File

class FinalDungeon(override val id: Int,
                   override val name: String,
                   override val description: String,
                   override val difficulty: Difficulty,
                   override val points: Int,
                   override val numberOfPlayers: IntRange,
                   override val box: Box,
                   override val startingLocation: BlockVector,
                   override val triggers: Map<Int, Trigger>,
                   override val activeAreas: Map<Int, ActiveArea>,
                   var instances: Map<Int, DungeonFinalInstance>) : Dungeon {

    var isActive = true
    var isBeingEdited = false

    fun putInEditMode(player: Player): EditableDungeon? {
        if (isActive) {
            player.sendFWDMessage(Strings.DUNGEON_WITH_ID_DISABLED.format(id))
            return null
        }

        if (isBeingEdited) {
            player.sendFWDMessage(Strings.DUNGEON_ALREADY_BEING_EDITED)
            return null
        }
        isBeingEdited = true

        player.sendFWDMessage(Strings.NOW_EDITING_DUNGEON_WITH_ID.format(id))

        return EditableDungeon(player).also {
            it.id = id
            it.name = name
            it.description = description
            it.difficulty = difficulty
            it.points = points
            it.numberOfPlayers = numberOfPlayers
            it.box = box.clone()
            it.startingLocation = startingLocation.clone()
            it.finalInstanceLocations = instances.values.map { ins -> ins.origin.clone() }.toMutableList()
            player.editableDungeon = it
            it.createTestInstance(player)
            it.triggers.putAll(triggers)
            it.activeAreas.putAll(activeAreas)
        }
    }

    fun import(at: BlockVector): Boolean {
        if (instances.isNotEmpty()) return false
        val config = YamlConfiguration()
        val file = File(FWDungeonsPlugin.pluginDataFolder, "instances.yml")
        if (file.exists()) config.load(file)
        val dgconf = config.createSection("$id")
        dgconf.createSection("$0").run {
            set("x", at.blockX)
            set("y", at.blockY)
            set("z", at.blockZ)
        }
        createInstance(ConfigManager.dungeonWorld.getBlockAt(at.blockX, at.blockY, at.blockZ))
        launchAsync { config.save(file) }
        return true
    }

    fun createInstance(target: Block): DungeonFinalInstance {
        val id = instances.keys.lastOrNull()?.plus(1) ?: 0
        val newOrigin = target.blockVector
        val newInstance = DungeonFinalInstance(id, this.id, newOrigin)
        newInstance.resetInstance()
        instances = instances + (id to newInstance)
        return newInstance
    }

    fun toConfig(conf: YamlConfiguration, eraseEffects: Boolean) {
        val dungeon = this
        conf.run {
            set("name", dungeon.name)
            set("description", dungeon.description)
            set("difficulty", dungeon.difficulty.toString())
            set("points", dungeon.points)
            set("numberOfPlayers", dungeon.numberOfPlayers.toList())
            set("width", dungeon.box.width)
            set("height", dungeon.box.height)
            set("depth", dungeon.box.depth)
            set("startingLocation", dungeon.startingLocation.toVector())
            dungeon.triggers.values.forEach {
                if (contains("triggers.${it.id}")) createSection("triggers.${it.id}")
                it.toConfig(getConfigurationSection("triggers.${it.id}")
                                ?: createSection("triggers.${it.id}"))
            }
            dungeon.activeAreas.values.forEach {
                if (contains("activeAreas.${it.id}")) createSection("activeAreas.${it.id}")
                it.toConfig(getConfigurationSection("activeAreas.${it.id}")
                        ?: createSection("activeAreas.${it.id}"))
            }
        }
    }

    companion object {

        val dungeons = mutableMapOf<Int, FinalDungeon>()

        fun fromConfig(id: Int, conf: YamlConfiguration): FinalDungeon = conf.run {
            val triggers = getConfigurationSection("triggers")!!
                    .getKeys(false)
                    .associate { it.toInt() to Trigger.fromConfig(it.toInt(), getConfigurationSection("triggers.$it")!!) }

            val activeAreas = getConfigurationSection("activeAreas")!!
                    .getKeys(false)
                    .associate { it.toInt() to ActiveArea.fromConfig(it.toInt(), getConfigurationSection("activeAreas.$it")!!) }

            val dungeon = FinalDungeon(
                    id,
                    getString("name")!!,
                    getString("description")!!,
                    Difficulty.fromString(getString("difficulty")!!)!!,
                    getInt("points", 0),
                    getIntegerList("numberOfPlayers").let { IntRange(it.first(), it.last()) },
                    Box(BlockVector(0, 0, 0), getInt("width"), getInt("height"), getInt("depth")),
                    getVector("startingLocation")!!.toBlockVector(),
                    triggers,
                    activeAreas,
                    mapOf()
            )

            dungeon
        }
    }
}