package it.forgottenworld.dungeons.command.edit.dungeon

import it.forgottenworld.dungeons.state.DungeonEditState
import it.forgottenworld.dungeons.utils.sendFWDMessage
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

fun cmdDungeonDescription(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
    if (sender !is Player) return true

    if (args.isEmpty()) {
        sender.sendFWDMessage("Not enough arguments: please provide a description")
        return true
    }

    val dungeon = DungeonEditState.dungeonEditors[sender.uniqueId] ?: run {
        sender.sendFWDMessage("You're not editing any dungeons")
        return true
    }

    dungeon.description = args.joinToString(" ")
    sender.sendFWDMessage("Dungeon description changed")

    return true
}