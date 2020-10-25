package it.forgottenworld.dungeons.command.edit.activearea

import it.forgottenworld.dungeons.manager.DungeonEditManager
import it.forgottenworld.dungeons.utils.sendFWDMessage
import org.bukkit.entity.Player

fun cmdActiveAreaLabel(sender: Player, args: Array<out String>): Boolean {
    val aaLabel = args.joinToString(" ").trim()

    if (aaLabel.isEmpty()) {
        sender.sendFWDMessage("Not enough arguments: please provide a non-whitespace only label")
        return true
    }

    val dungeon = DungeonEditManager.dungeonEditors[sender.uniqueId] ?: run {
        sender.sendFWDMessage("You're not editing any dungeons")
        return true
    }
    if (!DungeonEditManager.wipDungeons.contains(dungeon)) {
        sender.sendFWDMessage("This dungeon was already exported beforehand")
        return true
    }
    if (dungeon.activeAreas.isEmpty()) {
        sender.sendFWDMessage("This dungeon has no active areas yet")
        return true
    }

    dungeon.activeAreas.last().let {
        it.label = aaLabel
        DungeonEditManager.wipTestInstances[sender.uniqueId]?.activeAreas?.find { t -> t.id == it.id }?.label = aaLabel
        sender.sendFWDMessage("Set label $aaLabel")
    }

    return true
}