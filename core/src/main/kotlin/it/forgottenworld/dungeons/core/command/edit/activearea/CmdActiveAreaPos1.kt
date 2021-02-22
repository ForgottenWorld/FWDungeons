package it.forgottenworld.dungeons.core.command.edit.activearea

import it.forgottenworld.dungeons.api.command.PlayerCommand
import it.forgottenworld.dungeons.api.game.interactiveregion.InteractiveRegion
import it.forgottenworld.dungeons.core.command.edit.helpers.InteractiveRegionCommandHelper
import org.bukkit.entity.Player

class CmdActiveAreaPos1 : PlayerCommand() {

    override fun command(sender: Player, args: Array<out String>): Boolean {
        InteractiveRegionCommandHelper.setInteractiveRegionPos(sender, 1, InteractiveRegion.Type.ACTIVE_AREA)
        return true
    }
}