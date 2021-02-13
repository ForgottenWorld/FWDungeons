package it.forgottenworld.dungeons.command.edit.dungeon

import it.forgottenworld.dungeons.command.api.CommandHandler
import it.forgottenworld.dungeons.command.edit.helpers.DungeonBoxCommandHelper
import org.bukkit.entity.Player

class CmdDungeonPos1 : CommandHandler<Player> {

    override fun command(sender: Player, args: Array<out String>): Boolean {
        DungeonBoxCommandHelper.setDungeonBoxPos(sender, 1)
        return true
    }
}