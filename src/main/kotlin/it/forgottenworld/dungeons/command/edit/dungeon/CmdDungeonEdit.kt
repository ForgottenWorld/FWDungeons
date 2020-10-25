package it.forgottenworld.dungeons.command.edit.dungeon

import it.forgottenworld.dungeons.manager.DungeonEditManager
import it.forgottenworld.dungeons.manager.DungeonManager
import it.forgottenworld.dungeons.utils.sendFWDMessage
import org.bukkit.entity.Player

fun cmdDungeonEdit(sender: Player, args: Array<out String>): Boolean {
    if (args.isEmpty()) {
        sender.sendFWDMessage("Not enough arguments: please provide a dungeon id")
        return true
    }

    val id = args[0].toIntOrNull()
    if (id == null) {
        sender.sendFWDMessage("Dungeon id should be an integer")
        return true
    }

    val res = DungeonManager.getDungeonById(id)?.let { d ->
        when {
            DungeonEditManager.dungeonEditors.containsValue(d) -> false
            DungeonManager.activeDungeons[id] == true -> false
            else -> {
                DungeonEditManager.purgeWorkingData(sender)
                DungeonEditManager.dungeonEditors[sender.uniqueId] = d
                true
            }
        }
    } == true

    sender.sendFWDMessage(
            if (res) "Now editing dungeon with id $id"
            else "No currently disabled dungeons found with id $id"
    )

    return true
}