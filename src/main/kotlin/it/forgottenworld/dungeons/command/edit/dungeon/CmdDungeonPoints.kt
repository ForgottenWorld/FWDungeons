package it.forgottenworld.dungeons.command.edit.dungeon

import it.forgottenworld.dungeons.manager.DungeonEditManager
import it.forgottenworld.dungeons.utils.sendFWDMessage
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

fun cmdDungeonPoints(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
    if (sender !is Player) return true

    if (args.isEmpty()) {
        sender.sendFWDMessage("Not enough arguments: please provide an amount")
        return true
    }

    val points = args[0].toIntOrNull() ?: run {
        sender.sendFWDMessage("Invalid argument: amount of points should be an integer")
        return true
    }

    val dungeon = DungeonEditManager.dungeonEditors[sender.uniqueId] ?: run {
        sender.sendFWDMessage("You're not editing any dungeons")
        return true
    }

    dungeon.points = points
    sender.sendFWDMessage("Dungeon points changed")

    return true
}