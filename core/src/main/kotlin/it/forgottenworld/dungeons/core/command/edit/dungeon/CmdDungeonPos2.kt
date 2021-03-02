package it.forgottenworld.dungeons.core.command.edit.dungeon

import com.google.inject.Inject
import it.forgottenworld.dungeons.api.command.PlayerCommand
import it.forgottenworld.dungeons.core.command.edit.helpers.DungeonBoxCommandHelper
import org.bukkit.entity.Player

class CmdDungeonPos2 @Inject constructor(
    private val dungeonBoxCommandHelper: DungeonBoxCommandHelper
) : PlayerCommand() {

    override fun command(sender: Player, args: Array<out String>): Boolean {
        dungeonBoxCommandHelper.setDungeonBoxPos(sender, 2)
        return true
    }
}