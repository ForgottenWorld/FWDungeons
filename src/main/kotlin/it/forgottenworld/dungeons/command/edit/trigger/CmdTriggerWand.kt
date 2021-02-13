package it.forgottenworld.dungeons.command.edit.trigger

import it.forgottenworld.dungeons.command.api.CommandHandler
import it.forgottenworld.dungeons.command.edit.helpers.InteractiveRegionCommandHelper
import it.forgottenworld.dungeons.game.interactiveregion.InteractiveRegion
import org.bukkit.entity.Player

class CmdTriggerWand : CommandHandler<Player> {

    override fun command(sender: Player, args: Array<out String>): Boolean {
        InteractiveRegionCommandHelper.grantWandForInteractiveRegion(sender, InteractiveRegion.Type.TRIGGER)
        return true
    }
}