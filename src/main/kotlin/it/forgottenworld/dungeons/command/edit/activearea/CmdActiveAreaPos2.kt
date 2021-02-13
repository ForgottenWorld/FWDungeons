package it.forgottenworld.dungeons.command.edit.activearea

import it.forgottenworld.dungeons.command.api.CommandHandler
import it.forgottenworld.dungeons.command.edit.helpers.InteractiveRegionCommandHelper
import it.forgottenworld.dungeons.game.interactiveregion.InteractiveRegion
import org.bukkit.entity.Player

class CmdActiveAreaPos2 : CommandHandler<Player> {

    override fun command(sender: Player, args: Array<out String>): Boolean {
        InteractiveRegionCommandHelper.setInteractiveRegionPos(sender, 2, InteractiveRegion.Type.ACTIVE_AREA)
        return true
    }
}