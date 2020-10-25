package it.forgottenworld.dungeons.command.edit.dungeon

import it.forgottenworld.dungeons.manager.DungeonEditManager
import it.forgottenworld.dungeons.utils.sendFWDMessage
import org.bukkit.entity.Player

fun cmdDungeonNumberOfPlayers(sender: Player, args: Array<out String>): Boolean {
    if (args.count() < 2) {
        sender.sendFWDMessage("Not enough arguments: please provide minimum and maximum players")
        return true
    }

    val r1 = args[0].toIntOrNull()
    val r2 = args[1].toIntOrNull()
    if (r1 == null || r2 == null) {
        sender.sendFWDMessage("Please provide minimum and maximum players as integers")
        return true
    }

    val dungeon = DungeonEditManager.dungeonEditors[sender.uniqueId] ?: run {
        sender.sendFWDMessage("You're not editing any dungeons")
        return true
    }

    dungeon.numberOfPlayers = r1..r2
    sender.sendFWDMessage("Dungeon number of players changed")

    return true
}