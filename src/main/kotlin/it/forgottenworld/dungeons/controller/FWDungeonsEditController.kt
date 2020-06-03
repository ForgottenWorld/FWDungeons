package it.forgottenworld.dungeons.controller

import it.forgottenworld.dungeons.FWDungeonsPlugin
import it.forgottenworld.dungeons.config.ConfigManager
import it.forgottenworld.dungeons.model.activearea.ActiveArea
import it.forgottenworld.dungeons.model.box.Box
import it.forgottenworld.dungeons.model.dungeon.Dungeon
import it.forgottenworld.dungeons.model.dungeon.DungeonInstance
import it.forgottenworld.dungeons.model.trigger.Trigger
import it.forgottenworld.dungeons.utils.minBlockVector
import it.forgottenworld.dungeons.utils.repeatedlySpawnParticles
import it.forgottenworld.dungeons.utils.toVector
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.util.BlockVector
import java.util.*

object FWDungeonsEditController {
    private val dungeonEditors = mutableMapOf<UUID, Dungeon>()
    private val testInstances = mutableListOf<DungeonInstance>()
    private val wipDungeons = mutableListOf<Dungeon>()
    private val wipDungeonPos1s = mutableMapOf<UUID, Block>()
    private val wipDungeonPos2s = mutableMapOf<UUID, Block>()
    private val wipTriggerPos1s = mutableMapOf<UUID, Block>()
    private val wipTriggerPos2s = mutableMapOf<UUID, Block>()
    private val wipActiveAreaPos1s = mutableMapOf<UUID, Block>()
    private val wipActiveAreaPos2s = mutableMapOf<UUID, Block>()
    private val wipDungeonOrigins = mutableMapOf<UUID, BlockVector>()

    fun playerEditDungeon(player: Player, dungeonId: Int) : Boolean {
        return FWDungeonsController.getDungeonById(dungeonId)?.let {d ->
            if (dungeonEditors.containsValue(d))
                false
            else {
                dungeonEditors[player.uniqueId] = d
                true
            }
        } ?: false
    }

    fun playerCreateDungeon(player: Player) : Int {
        var newId: Int = FWDungeonsController.getMaxDungeonId() + 1
        while (wipDungeons.find{ it.id == newId } != null) {
            newId += 1
        }
        Dungeon(newId).let {
            wipDungeons.add(it)
            dungeonEditors.put(player.uniqueId, it)
        }
        return newId
    }

    fun playerSetDungeonPos1(player: Player, block: Block) : Int {
        val dungeon = dungeonEditors[player.uniqueId] ?: return -1 //player not editing a dungeon

        return wipDungeonPos2s[player.uniqueId]?.let {
            dungeon.box = Box(block, it).withOriginZero()
            wipDungeonOrigins[player.uniqueId] = minBlockVector(block, it)
            wipDungeonPos2s.remove(player.uniqueId)
            0 //dungeon box set succesfully
        } ?: ({
            wipDungeonPos1s[player.uniqueId] = block
            -2 //other position still needs to be selected
        })()
    }

    fun playerSetDungeonPos2(player: Player, block: Block) : Int {
        val dungeon = dungeonEditors[player.uniqueId] ?: return -1 //player not editing a dungeon

        return wipDungeonPos1s[player.uniqueId]?.let {
            dungeon.box = Box(it, block).withOriginZero()
            wipDungeonOrigins[player.uniqueId] = minBlockVector(block, it)
            wipDungeonPos1s.remove(player.uniqueId)
            0 //dungeon box set succesfully
        } ?: ({
            wipDungeonPos2s[player.uniqueId] = block
            -2 //other position still needs to be selected
        })()
    }

    fun playerSetTriggerPos1(player: Player, block: Block) : Int {
        val dungeon = dungeonEditors[player.uniqueId] ?: return -1 //player not editing a dungeon

        if (!dungeon.hasBox()) return -3 //dungeon has no box set yet

        var wipOrigin: BlockVector? = null
        if (wipDungeonOrigins[player.uniqueId]?.let {
                    wipOrigin = it
                    dungeon.box.withOrigin(it).containsBlock(block)

                } != true) {
            return -4 //target is outside of dungeon box
        }

        return wipTriggerPos2s[player.uniqueId]?.let { p2 ->
            val id = (dungeon.triggers.maxBy { it.id }?.id?.plus(1)) ?: 0
            dungeon.triggers.add(
                    Trigger(
                            id,
                            dungeon,
                            Box(block, p2).withContainerOrigin(wipOrigin!!,BlockVector(0,0,0)),
                            {},
                            false
                    ).apply {
                        box.withContainerOrigin(BlockVector(0,0,0), wipOrigin!!).highlightAll()
                    }
            )
            wipTriggerPos2s.remove(player.uniqueId)
            id //return the trigger id
        } ?: ({
            wipTriggerPos1s[player.uniqueId] = block
            -2 //other position still needs to be selected
        })()
    }

    fun playerSetTriggerPos2(player: Player, block: Block) : Int {
        val dungeon = dungeonEditors[player.uniqueId] ?: return -1 //player not editing a dungeon

        if (!dungeon.hasBox()) return -3 //dungeon has no box set yet

        var wipOrigin: BlockVector? = null
        if (wipDungeonOrigins[player.uniqueId]?.let {
                    wipOrigin = it
                    dungeon.box.withOrigin(it).containsBlock(block)
                } != true) {
            return -4 //target is outside of dungeon box
        }


        return wipTriggerPos1s[player.uniqueId]?.let { p1 ->
            val id = (dungeon.triggers.maxBy { it.id }?.id?.plus(1)) ?: 0
            dungeon.triggers.add(
                    Trigger(
                            id,
                            dungeon,
                            Box(p1, block).withContainerOrigin(wipOrigin!!,BlockVector(0,0,0)),
                            {},
                            false
                    ).apply {
                        box.withContainerOrigin(BlockVector(0,0,0),wipOrigin!!).highlightAll()
                    }
            )
            wipTriggerPos1s.remove(player.uniqueId)
            id //return the trigger id
        } ?: ({
            wipTriggerPos2s[player.uniqueId] = block
            -2 //other position still needs to be selected
        })()
    }

    fun playerSetActiveAreaPos1(player: Player, block: Block) : Int {
        val dungeon = dungeonEditors[player.uniqueId] ?: return -1 //player not editing a dungeon

        if (!dungeon.hasBox()) return -3 //dungeon has no box set yet

        var newBlock: Block? = null
        if (wipDungeonOrigins[player.uniqueId]?.let {
                    newBlock = block.world.getBlockAt(
                            block.location.subtract(
                                    it.toVector())
                    )
                    dungeon.box.withOrigin(it).containsBlock(block)
                } != true) {
            return -4 //target is outside of dungeon box
        }

        return wipActiveAreaPos2s[player.uniqueId]?.let { p2 ->
            val id = (dungeon.activeAreas.maxBy { it.id }?.id?.plus(1)) ?: 0
            dungeon.activeAreas.add(
                    ActiveArea(
                            id,
                            Box(newBlock!!, p2)
                    )
            )
            wipActiveAreaPos2s.remove(player.uniqueId)
            id //return the trigger id
        } ?: ({
            wipActiveAreaPos1s[player.uniqueId] = newBlock!!
            -2 //other position still needs to be selected
        })()
    }

    fun playerSetActiveAreaPos2(player: Player, block: Block) : Int {
        val dungeon = dungeonEditors[player.uniqueId] ?: return -1 //player not editing a dungeon

        if (!dungeon.hasBox()) return -3 //dungeon has no box set yet

        var newBlock: Block? = null
        if (wipDungeonOrigins[player.uniqueId]?.let {
                    newBlock = block.world.getBlockAt(
                            block.location.subtract(
                                    it.toVector())
                    )
                    dungeon.box.withOrigin(it).containsBlock(block)
                } != true) {
            return -4 //target is outside of dungeon box
        }

        return wipActiveAreaPos1s[player.uniqueId]?.let { p1 ->
            val id = (dungeon.activeAreas.maxBy { it.id }?.id?.plus(1)) ?: 0
            dungeon.activeAreas.add(
                    ActiveArea(
                            id,
                            Box(p1, newBlock!!)
                    )
            )
            wipActiveAreaPos1s.remove(player.uniqueId)
            id //return the trigger id
        } ?: ({
            wipActiveAreaPos2s[player.uniqueId] = block
            -2 //other position still needs to be selected
        })()
    }

    fun playerAddInstance(player: Player, block: Block) : Int {
        val dungeon = dungeonEditors[player.uniqueId] ?: return -1 //player not editing a dungeon

        if (wipDungeons.contains(dungeon)) {
            return -2 //dungeon is still being created
        }

        val id = dungeon.instances.maxBy { it.id }?.id?.plus(1) ?: 0
        dungeon.instances.add(
                DungeonInstance(
                        id,
                        dungeon,
                        block.location.toVector().toBlockVector(),
                        dungeon.triggers.map {
                            Trigger(it.id,
                                    it.dungeon,
                                    it.box.withOrigin(
                                            block.location
                                                    .toVector()
                                                    .add(it.origin)
                                                    .toBlockVector()),
                                    it.effect,
                                    it.requiresWholeParty
                            )
                        },
                        dungeon.activeAreas.map {
                            ActiveArea(it.id,
                                    it.box.withOrigin(
                                            block.location
                                                    .toVector()
                                                    .add(it.box.origin)
                                                    .toBlockVector())
                            )
                        }
        ))

        return id
    }

    fun playerRemoveInstance(player: Player) : Int {
        val dungeon = dungeonEditors[player.uniqueId] ?: return -1 //player not editing a dungeon

        if (wipDungeons.contains(dungeon)) {
            return -2 //dungeon is still being created
        }

        if (dungeon.instances.isEmpty()) {
            return -3 //dungeon has no instances
        }

        var id : Int = -4 //defaults to instance not found
        dungeon.instances.removeIf {
            if (it.box.containsPlayer(player)) {
                id = it.id
                true
            } else false
        }

        return id
    }

    fun playerNameDungeon(player: Player, name: String) : Int {
        val dungeon = dungeonEditors[player.uniqueId] ?: return -1 //player not editing a dungeon

        FWDungeonsController.dungeons.values.find {
            it.name.equals(name.trim(), true)
        }?.let { return -2 } //another dungeon with the same name already exists
        wipDungeons.find {
            it.name.equals(name.trim(), true)
        }?.let { return -3 } //another dungeon with the same name is being created

        dungeon.name = name
        return 0
    }

    fun playerWriteOutDungeon(player: Player) : String {
        val dungeon = dungeonEditors[player.uniqueId] ?:
            return "You're not editing any dungeons"
        if (!wipDungeons.contains(dungeon))
            return "This dungeon was already exported beforehand"

        val whatIsMissing = dungeon.whatIsMissingForWriteout()
        return if (whatIsMissing != "") {
            "Can't writeout yet, missing: $whatIsMissing"
        } else {
            ConfigManager.saveDungeonConfig(
                    FWDungeonsPlugin.dataFolder,
                    dungeon,
                    true
            )
            "Dungeon succesfully exported"
        }
    }

    fun playerSetStartDungeon(player: Player) : Int {
        val dungeon = dungeonEditors[player.uniqueId] ?: return -1 //player is not editing any dungeons
        if (!dungeon.hasBox()) return -2 //dungeon has no box set yet

        var newBlock: Block? = null
        if (wipDungeonOrigins[player.uniqueId]?.let {
                    newBlock = player.world.getBlockAt(
                            player.location.subtract(
                                    it.toVector())
                    )
                    dungeon.box.withOrigin(it).containsPlayer(player)

                } != true) {
            return -3 //target is outside of dungeon box
        }

        dungeon.startingLocation = BlockVector(newBlock!!.location.toVector())
        return 0
    }
}