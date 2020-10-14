package it.forgottenworld.dungeons.command.edit.trigger

import it.forgottenworld.dungeons.model.box.Box
import it.forgottenworld.dungeons.model.trigger.Trigger
import it.forgottenworld.dungeons.state.DungeonEditState
import it.forgottenworld.dungeons.utils.sendFWDMessage
import it.forgottenworld.dungeons.utils.targetBlock
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.util.BlockVector

fun cmdTriggerPos1(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
    if (sender !is Player) return true
    val block = sender.targetBlock

    if (block.blockData.material == Material.AIR) {
        sender.sendFWDMessage("You need to be targeting a block within 5 blocks of you before calling this")
        return true
    }

    val dungeon = DungeonEditState.dungeonEditors[sender.uniqueId] ?: run {
        sender.sendFWDMessage("You're not editing any dungeons")
        return true
    }

    if (!DungeonEditState.wipDungeons.contains(dungeon)) run {
        sender.sendFWDMessage("This dungeon was already exported beforehand")
        return true
    }

    if (!dungeon.hasBox) run {
        sender.sendFWDMessage("Dungeon box should be set before adding triggers")
        return true
    }

    var wipOrigin: BlockVector? = null
    if (DungeonEditState.wipDungeonOrigins[sender.uniqueId]?.let {
                wipOrigin = it
                dungeon.box.withOrigin(it).containsBlock(block)
            } != true) {
        sender.sendFWDMessage("Target is not inside the dungeon box")
        return true
    }

    DungeonEditState.wipTriggerPos2s[sender.uniqueId]?.let { p2 ->
        val id = (dungeon.triggers.maxByOrNull { it.id }?.id?.plus(1)) ?: 0
        val box = Box(block, p2)
        dungeon.triggers.add(Trigger(
                id,
                    dungeon,
                    box.withContainerOrigin(wipOrigin!!, BlockVector(0,0,0)),
                    null,
                    false
        ))
        DungeonEditState.wipTestInstances[sender.uniqueId]?.run {
            triggers[id] = Trigger(
                    id,
                    dungeon,
                    box,
                    null,
                    false
            ).also { it.applyMeta(this) }
            updateHlBlocks()
        }
        box.highlightAll()
        DungeonEditState.wipTriggerPos2s.remove(sender.uniqueId)
        sender.sendFWDMessage("Created trigger with id $id")
    } ?: run {
        DungeonEditState.wipTriggerPos1s[sender.uniqueId] = block
        sender.sendFWDMessage("First position set, now pick another with /fwde trigger pos2")
    }

    return true
}