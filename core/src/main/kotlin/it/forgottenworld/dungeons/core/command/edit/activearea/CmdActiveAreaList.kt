package it.forgottenworld.dungeons.core.command.edit.activearea

import com.google.inject.Inject
import it.forgottenworld.dungeons.api.command.PlayerCommand
import it.forgottenworld.dungeons.core.cli.InteractiveRegionListGuiGenerator
import it.forgottenworld.dungeons.core.config.Strings
import it.forgottenworld.dungeons.core.game.DungeonManager
import it.forgottenworld.dungeons.core.utils.sendFWDMessage
import it.forgottenworld.dungeons.core.utils.sendJsonMessage
import org.bukkit.entity.Player

class CmdActiveAreaList @Inject constructor(
    private val interactiveRegionListGuiGenerator: InteractiveRegionListGuiGenerator,
    private val dungeonManager: DungeonManager
) : PlayerCommand() {

    override fun command(sender: Player, args: Array<out String>): Boolean {
        val dungeon = dungeonManager.getPlayerEditableDungeon(sender.uniqueId) ?: run {
            sender.sendFWDMessage(Strings.NOT_EDITING_ANY_DUNGEONS)
            return true
        }

        sender.sendJsonMessage(
            interactiveRegionListGuiGenerator.showActiveAreas(
                dungeon,
                args
                    .getOrNull(0)
                    ?.toIntOrNull()
                    ?: 0
            )
        )
        return true
    }
}