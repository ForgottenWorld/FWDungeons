package it.forgottenworld.dungeons.core.command.edit.spawnarea

import com.google.inject.Inject
import it.forgottenworld.dungeons.api.command.PlayerCommand
import it.forgottenworld.dungeons.api.game.dungeon.subelement.interactiveregion.InteractiveRegion
import it.forgottenworld.dungeons.core.command.edit.helpers.InteractiveRegionCommandHelper
import org.bukkit.entity.Player

class CmdSpawnAreaWand @Inject constructor(
    private val interactiveRegionCommandHelper: InteractiveRegionCommandHelper
) : PlayerCommand() {

    override fun command(sender: Player, args: Array<out String>): Boolean {
        interactiveRegionCommandHelper.grantWandForInteractiveRegion(
            sender,
            InteractiveRegion.Type.SPAWN_AREA
        )
        return true
    }
}