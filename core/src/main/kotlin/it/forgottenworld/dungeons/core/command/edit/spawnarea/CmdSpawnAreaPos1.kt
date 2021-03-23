package it.forgottenworld.dungeons.core.command.edit.spawnarea

import com.google.inject.Inject
import it.forgottenworld.dungeons.api.command.PlayerCommand
import it.forgottenworld.dungeons.api.game.interactiveregion.InteractiveRegion
import it.forgottenworld.dungeons.core.command.edit.helpers.InteractiveRegionCommandHelper
import org.bukkit.entity.Player

class CmdSpawnAreaPos1 @Inject constructor(
    private val interactiveRegionCommandHelper: InteractiveRegionCommandHelper
) : PlayerCommand() {

    override fun command(sender: Player, args: Array<out String>): Boolean {
        interactiveRegionCommandHelper.setInteractiveRegionPos(
            sender,
            1,
            InteractiveRegion.Type.SPAWN_AREA
        )
        return true
    }
}