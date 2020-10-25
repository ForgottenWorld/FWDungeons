package it.forgottenworld.dungeons.command.edit.trigger

import it.forgottenworld.dungeons.manager.DungeonEditManager
import it.forgottenworld.dungeons.utils.sendFWDMessage
import org.bukkit.entity.Player

fun cmdTriggerLabel(sender: Player, args: Array<out String>): Boolean {
    val tLabel = args.joinToString(" ").trim()

    if (tLabel.isEmpty()) {
        sender.sendFWDMessage("Not enough arguments: please provide a non-whitespace only label")
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

    if (dungeon.triggers.isEmpty()) run {
        sender.sendFWDMessage("This dungeon has no triggers yet")
        return true
    }

    dungeon.triggers.lastOrNull()?.let {
        it.label = tLabel
        DungeonEditManager.wipTestInstances[sender.uniqueId]?.triggers?.get(it.id)?.label = tLabel
        sender.sendFWDMessage("Set label $tLabel")
    }

    return true
}