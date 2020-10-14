package it.forgottenworld.dungeons.command.edit.activearea

import it.forgottenworld.dungeons.state.DungeonEditState
import it.forgottenworld.dungeons.utils.sendFWDMessage
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

fun cmdActiveAreaLabel(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
    if (sender !is Player) return true
    val aaLabel = args.joinToString(" ").trim()

    if (aaLabel.isEmpty()) {
        sender.sendFWDMessage("Not enough arguments: please provide a non-whitespace only label")
        return true
    }

    val dungeon = DungeonEditState.dungeonEditors[sender.uniqueId] ?: run {
        sender.sendFWDMessage("You're not editing any dungeons")
        return true
    }
    if (!DungeonEditState.wipDungeons.contains(dungeon)) {
        sender.sendFWDMessage("This dungeon was already exported beforehand")
        return true
    }
    if (dungeon.activeAreas.isEmpty()) {
        sender.sendFWDMessage("This dungeon has no active areas yet")
        return true
    }

    dungeon.activeAreas.last().let {
        it.label = label
        DungeonEditState.wipTestInstances[sender.uniqueId]?.activeAreas?.find { t -> t.id == it.id }?.label = label
        sender.sendFWDMessage("Set label $aaLabel")
    }

    return true
}