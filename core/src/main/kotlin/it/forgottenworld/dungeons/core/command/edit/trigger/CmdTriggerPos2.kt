package it.forgottenworld.dungeons.core.command.edit.trigger

import it.forgottenworld.dungeons.api.command.PlayerCommand
import it.forgottenworld.dungeons.core.command.edit.helpers.InteractiveRegionCommandHelper
import it.forgottenworld.dungeons.api.game.interactiveregion.InteractiveRegion
import org.bukkit.entity.Player

class CmdTriggerPos2 : PlayerCommand() {

    override fun command(sender: Player, args: Array<out String>): Boolean {
        InteractiveRegionCommandHelper.setInteractiveRegionPos(sender, 2, InteractiveRegion.Type.TRIGGER)
        return true
    }
}