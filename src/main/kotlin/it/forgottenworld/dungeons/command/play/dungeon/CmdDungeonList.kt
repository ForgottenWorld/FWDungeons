package it.forgottenworld.dungeons.command.play.dungeon

import it.forgottenworld.dungeons.cli.getInteractiveDungeonList
import org.bukkit.entity.Player

fun cmdDungeonList(sender: Player, args: Array<out String>): Boolean {
    val page = if (args.count() != 0 && args[0].toIntOrNull() != null) args[0].toInt() else 0
    sender.spigot().sendMessage(*getInteractiveDungeonList(page))
    return true
}