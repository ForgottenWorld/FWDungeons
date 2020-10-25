package it.forgottenworld.dungeons.command.edit.trigger

import it.forgottenworld.dungeons.manager.DungeonEditManager
import it.forgottenworld.dungeons.model.Box
import it.forgottenworld.dungeons.model.Trigger
import it.forgottenworld.dungeons.utils.sendFWDMessage
import it.forgottenworld.dungeons.utils.targetBlock
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.util.BlockVector

fun cmdTriggerPos2(sender: Player, args: Array<out String>): Boolean {
    val block = sender.targetBlock

    if (block.blockData.material == Material.AIR) {
        sender.sendFWDMessage("You need to be targeting a block within 5 blocks of you before calling this")
        return true
    }

    val dungeon = DungeonEditManager.dungeonEditors[sender.uniqueId] ?: run {
        sender.sendFWDMessage("You're not editing any dungeons")
        return true
    }

    if (!DungeonEditManager.wipDungeons.contains(dungeon)) run {
        sender.sendFWDMessage("This dungeon was already exported beforehand")
        return true
    }

    if (!dungeon.hasBox) run {
        sender.sendFWDMessage("Dungeon box should be set before adding triggers")
        return true
    }

    var wipOrigin: BlockVector? = null
    if (DungeonEditManager.wipDungeonOrigins[sender.uniqueId]?.let {
                wipOrigin = it
                dungeon.box.withOrigin(it).containsBlock(block)
            } != true) {
        sender.sendFWDMessage("Target is not inside the dungeon box")
        return true
    }

    DungeonEditManager.wipTriggerPos1s[sender.uniqueId]?.let { p1 ->
        val id = (dungeon.triggers.maxByOrNull { it.id }?.id?.plus(1)) ?: 0
        val box = Box(p1, block)
        dungeon.triggers.add(
                Trigger(
                        id,
                        dungeon,
                        box.withContainerOrigin(wipOrigin!!, BlockVector(0,0,0)),
                        null,
                        false
                )
        )
        DungeonEditManager.wipTestInstances[sender.uniqueId]?.run{
            triggers[id] = Trigger(
                    id,
                    dungeon,
                    box,
                    null,
                    false
            ).also { it.applyMeta() }
            updateHlBlocks()
        }
        box.highlightAll()
        DungeonEditManager.wipTriggerPos1s.remove(sender.uniqueId)
        sender.sendFWDMessage("Created trigger with id $id")
    } ?: run {
        DungeonEditManager.wipTriggerPos2s[sender.uniqueId] = block
        sender.sendFWDMessage("First position set, now pick another with /fwde trigger pos1")
    }

    return true
}