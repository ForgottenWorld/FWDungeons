package it.forgottenworld.dungeons.command.edit.activearea

import it.forgottenworld.dungeons.command.api.CommandHandler
import it.forgottenworld.dungeons.command.edit.helpers.InteractiveRegionCommandHelper
import it.forgottenworld.dungeons.game.interactiveregion.InteractiveRegion
import org.bukkit.entity.Player

class CmdActiveAreaPos1 : CommandHandler<Player> {

    override fun command(sender: Player, args: Array<out String>): Boolean {
        InteractiveRegionCommandHelper.setInteractiveRegionPos(sender, 1, InteractiveRegion.Type.ACTIVE_AREA)
        return true
    }
}