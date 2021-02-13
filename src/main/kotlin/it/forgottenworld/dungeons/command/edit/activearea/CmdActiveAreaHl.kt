package it.forgottenworld.dungeons.command.edit.activearea

import it.forgottenworld.dungeons.command.api.CommandHandler
import it.forgottenworld.dungeons.command.edit.helpers.InteractiveRegionCommandHelper
import it.forgottenworld.dungeons.config.Strings
import it.forgottenworld.dungeons.game.interactiveregion.InteractiveRegion
import it.forgottenworld.dungeons.utils.sendFWDMessage
import org.bukkit.entity.Player

class CmdActiveAreaHl : CommandHandler<Player> {

    override fun command(sender: Player, args: Array<out String>): Boolean {
        val id = args.getOrNull(0)?.toIntOrNull() ?: run {
            sender.sendFWDMessage(Strings.PROVIDE_VALID_ACTIVE_AREA_ID)
            return true
        }

        InteractiveRegionCommandHelper.highlightInteractiveRegion(sender, InteractiveRegion.Type.ACTIVE_AREA, id)
        return true
    }
}