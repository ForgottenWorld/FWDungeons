package it.forgottenworld.dungeons.command.play.dungeon

import it.forgottenworld.dungeons.cli.DungeonListGui
import it.forgottenworld.dungeons.command.api.CommandHandler
import org.bukkit.entity.Player

class CmdDungeonList: CommandHandler<Player> {

    override fun command(sender: Player, args: Array<out String>): Boolean {
        val page = if (args.count() != 0 && args[0].toIntOrNull() != null) args[0].toInt() else 0
        sender.spigot().sendMessage(*DungeonListGui.showPage(page))
        return true
    }
}