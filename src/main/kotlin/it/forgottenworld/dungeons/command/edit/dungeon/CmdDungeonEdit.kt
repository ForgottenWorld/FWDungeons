package it.forgottenworld.dungeons.command.edit.dungeon

import it.forgottenworld.dungeons.service.DungeonEditService
import it.forgottenworld.dungeons.service.DungeonService
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

    if (DungeonEditService.wipDungeons.containsKey(sender.uniqueId)) {
        sender.sendFWDMessage("You're already editing a dungeon")
        return true
    }

    val res = DungeonService.dungeons[id]?.let { d ->
        when {
            d.active -> false
            else -> {
                DungeonEditService.wipDungeons[sender.uniqueId] = d.makeEditable()!!
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