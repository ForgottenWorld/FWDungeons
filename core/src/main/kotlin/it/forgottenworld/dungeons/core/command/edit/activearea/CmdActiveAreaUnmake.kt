package it.forgottenworld.dungeons.core.command.edit.activearea

import com.google.inject.Inject
import it.forgottenworld.dungeons.api.command.PlayerCommand
import it.forgottenworld.dungeons.api.game.interactiveregion.InteractiveRegion
import it.forgottenworld.dungeons.core.command.edit.helpers.InteractiveRegionCommandHelper
import org.bukkit.entity.Player

class CmdActiveAreaUnmake @Inject constructor(
    private val interactiveRegionCommandHelper: InteractiveRegionCommandHelper
) : PlayerCommand() {

    override fun command(sender: Player, args: Array<out String>): Boolean {
        interactiveRegionCommandHelper.unMakeInteractiveRegion(
            sender,
            InteractiveRegion.Type.ACTIVE_AREA,
            args.getOrNull(0)?.toIntOrNull()
        )
        return true
    }
}