package it.forgottenworld.dungeons.core.command.edit.dungeon

import it.forgottenworld.dungeons.api.command.PlayerCommand
import it.forgottenworld.dungeons.core.command.edit.helpers.DungeonBoxCommandHelper
import org.bukkit.entity.Player

class CmdDungeonPos2 : PlayerCommand() {

    override fun command(sender: Player, args: Array<out String>): Boolean {
        DungeonBoxCommandHelper.setDungeonBoxPos(sender, 2)
        return true
    }
}