package it.forgottenworld.dungeons.command.edit.activearea

import it.forgottenworld.dungeons.command.api.CommandHandler
import it.forgottenworld.dungeons.command.edit.helpers.InteractiveRegionCommandHelper
import it.forgottenworld.dungeons.game.interactiveregion.InteractiveRegion
import org.bukkit.entity.Player

class CmdActiveAreaUnmake : CommandHandler<Player> {

    override fun command(sender: Player, args: Array<out String>): Boolean {
        InteractiveRegionCommandHelper.unMakeInteractiveRegion(
            sender,
            InteractiveRegion.Type.ACTIVE_AREA,
            args.getOrNull(0)?.toIntOrNull()
        )
        return true
    }
}