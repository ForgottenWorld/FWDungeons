package it.forgottenworld.dungeons.core.command.play.dungeon

import it.forgottenworld.dungeons.core.cli.DungeonListGui
import it.forgottenworld.dungeons.api.command.PlayerCommand
import it.forgottenworld.dungeons.core.utils.sendJsonMessage
import org.bukkit.entity.Player

class CmdDungeonList: PlayerCommand() {

    override fun command(sender: Player, args: Array<out String>): Boolean {
        val page = if (args.count() != 0 && args[0].toIntOrNull() != null) args[0].toInt() else 0
        sender.sendJsonMessage(DungeonListGui.showPage(page))
        return true
    }
}