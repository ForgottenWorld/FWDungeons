package it.forgottenworld.dungeons.command.edit.activearea

import it.forgottenworld.dungeons.state.DungeonEditState
import it.forgottenworld.dungeons.utils.sendFWDMessage
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

fun cmdActiveAreaUnmake(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
    if (sender !is Player) return true

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

    dungeon.activeAreas.lastOrNull()?.let {
        dungeon.activeAreas.remove(it)
        DungeonEditState.wipTestInstances[sender.uniqueId]?.run {
            activeAreas.removeLast()
            updateHlBlocks()
        }
        sender.sendFWDMessage("Deleted active area with id ${it.id}")
    }

    return true
}