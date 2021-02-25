package it.forgottenworld.dungeons.core.game.dungeon

import it.forgottenworld.dungeons.api.game.chest.Chest
import it.forgottenworld.dungeons.api.game.dungeon.Dungeon
import it.forgottenworld.dungeons.api.math.Box
import it.forgottenworld.dungeons.api.math.Vector3i
import it.forgottenworld.dungeons.api.math.toVector
import it.forgottenworld.dungeons.api.math.toVector3i
import it.forgottenworld.dungeons.core.config.ConfigManager
import it.forgottenworld.dungeons.core.config.Strings
import it.forgottenworld.dungeons.core.game.chest.ChestImpl
import it.forgottenworld.dungeons.core.game.detection.CubeGridFactory.triggerGrid
import it.forgottenworld.dungeons.core.game.dungeon.DungeonManager.editableDungeon
import it.forgottenworld.dungeons.core.game.dungeon.DungeonManager.instances
import it.forgottenworld.dungeons.core.game.instance.DungeonInstanceImpl
import it.forgottenworld.dungeons.core.game.interactiveregion.ActiveAreaImpl
import it.forgottenworld.dungeons.core.game.interactiveregion.TriggerImpl
import it.forgottenworld.dungeons.core.utils.*
import org.bukkit.block.Block
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import java.io.File

class FinalDungeon(
    override val id: Int,
    override val name: String,
    override val description: String,
    override val difficulty: Dungeon.Difficulty,
    override val points: Int,
    override val numberOfPlayers: IntRange,
    override val box: Box,
    override val startingLocation: Vector3i,
    override val triggers: Map<Int, TriggerImpl>,
    override val activeAreas: Map<Int, ActiveAreaImpl>,
    override val chests: Map<Int, Chest>
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

        return EditableDungeon(
            player,
            id,
            name,
            description,
            difficulty,
            numberOfPlayers,
            box.clone(),
            startingLocation.copy(),
            points,
            instances.values.map { ins -> ins.origin.copy() }.toMutableList(),
            triggers,
            activeAreas
        ).also { player.editableDungeon = it }
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
        (launchAsync { config.save(file) })
        return true
    }

    fun createInstance(target: Block): DungeonInstanceImpl {
        val id = instances.keys.firstGap()
        val newInstance = DungeonInstanceImpl(id, this, target.vector3i)
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
                (it as ChestImpl).toConfig(createSection("chests.${it.id}"))
            }
        }
    }

    companion object {

        fun fromConfig(id: Int, conf: YamlConfiguration) = conf.run {

            val triggers = getConfigurationSection("triggers")
                ?.getKeys(false)
                ?.associate {
                    it.toInt() to TriggerImpl.fromConfig(
                        it.toInt(),
                        getConfigurationSection("triggers.$it")!!
                    )
                }
                ?: mapOf()

            val activeAreas = getConfigurationSection("activeAreas")
                ?.getKeys(false)
                ?.associate {
                    it.toInt() to ActiveAreaImpl.fromConfig(
                        it.toInt(),
                        getConfigurationSection("activeAreas.$it")!!
                    )
                }
                ?: mapOf()

            val chests = getConfigurationSection("chests")
                ?.getKeys(false)
                ?.associate {
                    it.toInt() to ChestImpl.fromConfig(
                        getConfigurationSection("chests.$it")!!
                    )
                }
                ?: mapOf()

            val dungeon = FinalDungeon(
                id,
                getString("name")!!,
                getString("description")!!,
                Dungeon.Difficulty.fromString(getString("difficulty")!!)!!,
                getInt("points", 0),
                getIntegerList("numberOfPlayers").let { it.first()..it.last() },
                Box(
                    Vector3i.ZERO,
                    getInt("width"),
                    getInt("height"),
                    getInt("depth")
                ),
                getVector("startingLocation")!!.toVector3i(),
                triggers,
                activeAreas,
                chests
            )

            dungeon
        }
    }
}