package it.forgottenworld.dungeons.command.edit.activearea

import it.forgottenworld.dungeons.manager.DungeonEditManager
import it.forgottenworld.dungeons.utils.sendFWDMessage
import org.bukkit.entity.Player

fun cmdActiveAreaUnmake(sender: Player, args: Array<out String>): Boolean {
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

    dungeon.activeAreas.lastOrNull()?.let {
        dungeon.activeAreas.remove(it)
        DungeonEditManager.wipTestInstances[sender.uniqueId]?.run {
            activeAreas.removeLast()
            updateHlBlocks()
        }
        sender.sendFWDMessage("Deleted active area with id ${it.id}")
    }

    return true
}