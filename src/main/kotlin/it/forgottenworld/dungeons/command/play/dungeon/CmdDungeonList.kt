package it.forgottenworld.dungeons.command.play.dungeon

import it.forgottenworld.dungeons.cli.getInteractiveDungeonList
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

fun cmdDungeonList(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
    if (sender !is Player) return true
    val page = if (args.count() != 0 && args[0].toIntOrNull() != null) args[0].toInt() else 0
    sender.spigot().sendMessage(getInteractiveDungeonList(page))
    return true
}