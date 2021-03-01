package it.forgottenworld.dungeons.core.command.edit.activearea

import com.google.inject.Inject
import it.forgottenworld.dungeons.api.command.PlayerCommand
import it.forgottenworld.dungeons.api.game.interactiveregion.InteractiveRegion
import it.forgottenworld.dungeons.core.command.edit.helpers.InteractiveRegionCommandHelper
import it.forgottenworld.dungeons.core.config.Strings
import it.forgottenworld.dungeons.core.utils.sendFWDMessage
import org.bukkit.entity.Player

class CmdActiveAreaHl @Inject constructor(
    private val interactiveRegionCommandHelper: InteractiveRegionCommandHelper
) : PlayerCommand() {

    override fun command(sender: Player, args: Array<out String>): Boolean {
        val id = args.getOrNull(0)?.toIntOrNull() ?: run {
            sender.sendFWDMessage(Strings.PROVIDE_VALID_ACTIVE_AREA_ID)
            return true
        }

        interactiveRegionCommandHelper.highlightInteractiveRegion(
            sender,
            InteractiveRegion.Type.ACTIVE_AREA,
            id
        )
        return true
    }
}