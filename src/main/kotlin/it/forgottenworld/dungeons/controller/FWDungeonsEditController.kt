package it.forgottenworld.dungeons.controller

import it.forgottenworld.dungeons.model.box.Box
import it.forgottenworld.dungeons.model.dungeon.Dungeon
import it.forgottenworld.dungeons.model.dungeon.DungeonInstance
import it.forgottenworld.dungeons.model.trigger.Trigger
import it.forgottenworld.dungeons.utils.minBlockVector
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.util.BlockVector
import java.util.*

object FWDungeonsEditController {
    private val dungeonEditors = mutableMapOf<UUID, Dungeon>()
    private val wipDungeons = mutableListOf<Dungeon>()
    private val wipDungeonPos1s = mutableMapOf<UUID, Block>()
    private val wipDungeonPos2s = mutableMapOf<UUID, Block>()
    private val wipTriggerPos1s = mutableMapOf<UUID, Block>()
    private val wipTriggerPos2s = mutableMapOf<UUID, Block>()
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

        return if (wipDungeonPos2s.containsKey(player.uniqueId)) {
            dungeon.box = Box(block, wipDungeonPos2s[player.uniqueId]!!).withOriginZero()
            wipDungeonPos2s.remove(player.uniqueId)
            wipDungeonOrigins[player.uniqueId] = minBlockVector(block, wipDungeonPos2s[player.uniqueId]!!)
            0 //dungeon box set succesfully
        } else {
            wipDungeonPos1s[player.uniqueId] = block
            -2 //other position still needs to be selected
        }
    }

    fun playerSetDungeonPos2(player: Player, block: Block) : Int {
        val dungeon = dungeonEditors[player.uniqueId] ?: return -1 //player not editing a dungeon

        wipDungeonPos1s[player.uniqueId] = block
        return if (wipDungeonPos1s.containsKey(player.uniqueId)) {
            dungeon.box = Box(wipDungeonPos1s[player.uniqueId]!!, block).withOriginZero()
            wipDungeonPos1s.remove(player.uniqueId)
            wipDungeonOrigins[player.uniqueId] = minBlockVector(wipDungeonPos1s[player.uniqueId]!!, block)
            0 //dungeon box set succesfully
        } else {
            wipDungeonPos2s[player.uniqueId] = block
            -2 //other position still needs to be selected
        }
    }

    fun playerSetTriggerPos1(player: Player, block: Block) : Int {
        val dungeon = dungeonEditors[player.uniqueId] ?: return -1 //player not editing a dungeon

        if (!dungeon.hasBox()) return -3 //dungeon has no box set yet

        if (wipDungeonOrigins[player.uniqueId]?.let {
                    dungeon.box.withOrigin(it).containsBlock(block)
                } != true) {
            return -4 //target is outside of dungeon box
        }

        wipTriggerPos1s[player.uniqueId] = block
        return if (wipTriggerPos2s.containsKey(player.uniqueId)) {
            val id = (dungeon.triggers.maxBy { it.id }?.id?.plus(1)) ?: 0
            dungeon.triggers.add(
                    Trigger(
                            id,
                            dungeon,
                            Box(block, wipTriggerPos2s[player.uniqueId]!!),
                            {},
                            false
                    )
            )
            wipTriggerPos2s.remove(player.uniqueId)
            id //return the trigger id
        } else {
            -2 //other position still needs to be selected
        }
    }

    fun playerSetTriggerPos2(player: Player, block: Block) : Int {
        val dungeon = dungeonEditors[player.uniqueId] ?: return -1 //player not editing a dungeon

        if (!dungeon.hasBox()) return -3 //dungeon has no box set yet

        if (wipDungeonOrigins[player.uniqueId]?.let {
                    dungeon.box.withOrigin(it).containsBlock(block)
                } != true) {
            return -4 //target is outside of dungeon box
        }

        wipTriggerPos1s[player.uniqueId] = block
        return if (wipTriggerPos1s.containsKey(player.uniqueId)) {
            val id = (dungeon.triggers.maxBy { it.id }?.id?.plus(1)) ?: 0
            dungeon.triggers.add(
                    Trigger(
                            id,
                            dungeon,
                            Box(wipTriggerPos1s[player.uniqueId]!!, block),
                            {},
                            false
                    )
            )
            wipTriggerPos1s.remove(player.uniqueId)
            id //return the trigger id
        } else {
            -2 //other position still needs to be selected
        }
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

        dungeon.name = name

        return 0
    }
}