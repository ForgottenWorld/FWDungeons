package it.forgottenworld.dungeons.command.edit.trigger

import it.forgottenworld.dungeons.manager.DungeonEditManager
import it.forgottenworld.dungeons.utils.sendFWDMessage
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

fun cmdTriggerUnmake(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
    if (sender !is Player) return true

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

    dungeon.triggers.last().let {
        dungeon.triggers.remove(it)
        DungeonEditManager.wipTestInstances[sender.uniqueId]?.run {
            triggers.keys.maxOrNull()?.let { id ->
                updateHlBlocks()
                triggers.remove(id)
            }
        }
        sender.sendFWDMessage("Deleted trigger with id ${it.id}")
    }

    return true
}