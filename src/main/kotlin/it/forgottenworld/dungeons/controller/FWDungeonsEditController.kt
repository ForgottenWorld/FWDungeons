package it.forgottenworld.dungeons.controller

import it.forgottenworld.dungeons.model.box.Box
import it.forgottenworld.dungeons.model.dungeon.Dungeon
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

    fun playerStartEditing(player: Player, dungeonId: Int) : Boolean {
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
        var newId: Int = FWDungeonsController.getMaxDungeonId() ?: 0
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
        if (dungeonEditors.containsKey(player.uniqueId)) return -1 //player not editing a dungeon
        val dungeon = dungeonEditors[player.uniqueId]!!

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
        if (dungeonEditors.containsKey(player.uniqueId)) return -1 //player not editing a dungeon
        val dungeon = dungeonEditors[player.uniqueId]!!

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
        if (dungeonEditors.containsKey(player.uniqueId)) return -1 //player not editing a dungeon
        val dungeon = dungeonEditors[player.uniqueId]!!

        if (!dungeon.hasBox()) return -3 //dungeon has no box set yet

        if (wipDungeonOrigins[player.uniqueId]?.let {
                    dungeon.box.withOrigin(it).containsBlock(block)
                } != true) {
            if (!dungeon.hasBox()) return -4 //player is outside of dungeon box
        }

        wipTriggerPos1s[player.uniqueId] = block
        return if (wipTriggerPos2s.containsKey(player.uniqueId)) {
            val id = (dungeon.triggers.maxBy { it.id }?.id?.plus(1)) ?: 0
            dungeon.triggers.add(
                    Trigger(
                            id,
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
        if (dungeonEditors.containsKey(player.uniqueId)) return -1 //player not editing a dungeon
        val dungeon = dungeonEditors[player.uniqueId]!!

        if (!dungeon.hasBox()) return -3 //dungeon has no box set yet

        if (wipDungeonOrigins[player.uniqueId]?.let {
                    dungeon.box.withOrigin(it).containsBlock(block)
                } != true) {
            if (!dungeon.hasBox()) return -4 //player is outside of dungeon box
        }

        wipTriggerPos1s[player.uniqueId] = block
        return if (wipTriggerPos1s.containsKey(player.uniqueId)) {
            val id = (dungeon.triggers.maxBy { it.id }?.id?.plus(1)) ?: 0
            dungeon.triggers.add(
                    Trigger(
                            id,
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
}