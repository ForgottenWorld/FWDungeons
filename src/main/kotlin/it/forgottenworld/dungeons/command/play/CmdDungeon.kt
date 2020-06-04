package it.forgottenworld.dungeons.command.play

import it.forgottenworld.dungeons.cui.getInteractiveDungeonList
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

val dungeonCmdBindings: Map<String, (CommandSender, Command, String, Array<String>) -> Boolean> =
        mapOf(
                "joininst" to ::cmdDungeonJoinInstance,
                "list" to ::cmdDungeonList
        )

fun cmdDungeonList(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
    if (sender is Player) {
        val page = if (args.count() != 0 && args[0].toIntOrNull() != null) args[0].toInt() else 0
        sender.spigot().sendMessage(getInteractiveDungeonList(sender, page))
    }
    return true
}

fun cmdDungeonJoinInstance(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
    if (sender is Player) {

    }
    return true
}

