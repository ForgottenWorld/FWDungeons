package it.forgottenworld.dungeons.core.command.edit.spawnarea

import com.google.inject.Inject
import it.forgottenworld.dungeons.api.command.PlayerCommand
import it.forgottenworld.dungeons.api.game.interactiveregion.InteractiveRegion
import it.forgottenworld.dungeons.core.command.edit.helpers.InteractiveRegionCommandHelper
import it.forgottenworld.dungeons.core.storage.Strings
import it.forgottenworld.dungeons.core.utils.sendPrefixedMessage
import org.bukkit.entity.Player

class CmdSpawnAreaHl @Inject constructor(
    private val interactiveRegionCommandHelper: InteractiveRegionCommandHelper
) : PlayerCommand() {

    override fun command(sender: Player, args: Array<out String>): Boolean {
        val id = args.getOrNull(0)?.toIntOrNull() ?: run {
            sender.sendPrefixedMessage(Strings.PROVIDE_VALID_SPAWN_AREA_ID)
            return true
        }

        interactiveRegionCommandHelper.highlightInteractiveRegion(
            sender,
            InteractiveRegion.Type.SPAWN_AREA,
            id
        )
        return true
    }
}