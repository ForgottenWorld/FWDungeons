package it.forgottenworld.dungeons.core.command.edit.activearea

import it.forgottenworld.dungeons.api.command.PlayerCommand
import it.forgottenworld.dungeons.core.command.edit.helpers.InteractiveRegionCommandHelper
import it.forgottenworld.dungeons.api.game.interactiveregion.InteractiveRegion
import org.bukkit.entity.Player

class CmdActiveAreaUnmake : PlayerCommand() {

    override fun command(sender: Player, args: Array<out String>): Boolean {
        InteractiveRegionCommandHelper.unMakeInteractiveRegion(
            sender,
            InteractiveRegion.Type.ACTIVE_AREA,
            args.getOrNull(0)?.toIntOrNull()
        )
        return true
    }
}