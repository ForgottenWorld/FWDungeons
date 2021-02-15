package it.forgottenworld.dungeons.game.dungeon

import it.forgottenworld.dungeons.config.ConfigManager
import it.forgottenworld.dungeons.config.Strings
import it.forgottenworld.dungeons.game.box.Box
import it.forgottenworld.dungeons.game.chest.Chest
import it.forgottenworld.dungeons.game.detection.CubeGridUtils.triggerGrid
import it.forgottenworld.dungeons.game.dungeon.EditableDungeon.Companion.editableDungeon
import it.forgottenworld.dungeons.game.instance.DungeonFinalInstance
import it.forgottenworld.dungeons.game.interactiveregion.ActiveArea
import it.forgottenworld.dungeons.game.interactiveregion.Trigger
import it.forgottenworld.dungeons.utils.Vector3i
import it.forgottenworld.dungeons.utils.launchAsync
import it.forgottenworld.dungeons.utils.plugin
import it.forgottenworld.dungeons.utils.sendFWDMessage
import it.forgottenworld.dungeons.utils.toVector
import it.forgottenworld.dungeons.utils.toVector3i
import it.forgottenworld.dungeons.utils.vector3i
import org.bukkit.block.Block
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import java.io.File
import kotlin.reflect.KProperty

class FinalDungeon(
    override val id: Int,
    override val name: String,
    override val description: String,
    override val difficulty: Dungeon.Difficulty,
    override val points: Int,
    override val numberOfPlayers: IntRange,
    override val box: Box,
    override val startingLocation: Vector3i,
    override val triggers: Map<Int, Trigger>,
    override val activeAreas: Map<Int, ActiveArea>,
    override val chests: Map<Int, Chest>,
    var instances: Map<Int, DungeonFinalInstance>
) : Dungeon {

    var isActive = true
    var isBeingEdited = false
    val triggerGrid by triggerGrid()

    fun putInEditMode(player: Player): EditableDungeon? {
        if (isActive) {
            player.sendFWDMessage(Strings.DUNGEON_WITH_ID_NOT_DISABLED.format(id))
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
            it.startingLocation = startingLocation.copy()
            it.finalInstanceLocations = instances.values.map { ins -> ins.origin.copy() }.toMutableList()
            player.editableDungeon = it
            it.createTestInstance()
            it.triggers = triggers
            it.activeAreas = activeAreas
        }
    }

    fun import(at: Vector3i): Boolean {
        if (instances.isNotEmpty()) return false
        val config = YamlConfiguration()
        val file = File(plugin.dataFolder, "instances.yml")
        if (file.exists()) config.load(file)
        val dgconf = config.createSection("$id")
        dgconf.createSection("$0").run {
            set("x", at.x)
            set("y", at.y)
            set("z", at.z)
        }
        createInstance(ConfigManager.dungeonWorld.getBlockAt(at.x, at.y, at.z))
        @Suppress("BlockingMethodInNonBlockingContext")
        launchAsync { config.save(file) }
        return true
    }

    fun createInstance(target: Block): DungeonFinalInstance {
        val id = instances.keys.lastOrNull()?.plus(1) ?: 0
        val newOrigin = target.vector3i
        val newInstance = DungeonFinalInstance(id, this.id, newOrigin)
        newInstance.resetInstance()
        instances = instances + (id to newInstance)
        return newInstance
    }

    fun toConfig(conf: YamlConfiguration) {
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
                it.toConfig(createSection("triggers.${it.id}"))
            }
            dungeon.activeAreas.values.forEach {
                it.toConfig(createSection("activeAreas.${it.id}"))
            }
            dungeon.chests.values.forEach {
                it.toConfig(createSection("chests.${it.id}"))
            }
        }
    }

    class FinalDungeonsDelegate private constructor(val id: Int) {

        operator fun getValue(thisRef: Any?, property: KProperty<*>) =
            dungeons[id] ?: error("Dungeon $id was not found")

        companion object {
            fun finalDungeons(id: Int) = FinalDungeonsDelegate(id)
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
                .associate {
                    it.toInt() to ActiveArea.fromConfig(
                        it.toInt(),
                        getConfigurationSection("activeAreas.$it")!!
                    )
                }

            val chests = getConfigurationSection("chests")!!
                .getKeys(false)
                .associate {
                    it.toInt() to Chest.fromConfig(
                        getConfigurationSection("chests.$it")!!
                    )
                }

            val dungeon = FinalDungeon(
                id,
                getString("name")!!,
                getString("description")!!,
                Dungeon.Difficulty.fromString(getString("difficulty")!!)!!,
                getInt("points", 0),
                getIntegerList("numberOfPlayers").let { IntRange(it.first(), it.last()) },
                Box(Vector3i(0, 0, 0), getInt("width"), getInt("height"), getInt("depth")),
                getVector("startingLocation")!!.toVector3i(),
                triggers,
                activeAreas,
                chests,
                mapOf()
            )

            dungeon
        }
    }
}