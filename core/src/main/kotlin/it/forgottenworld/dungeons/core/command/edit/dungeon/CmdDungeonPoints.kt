package it.forgottenworld.dungeons.core.command.edit.dungeon

import com.google.inject.Inject
import it.forgottenworld.dungeons.api.command.PlayerCommand
import it.forgottenworld.dungeons.core.config.Strings
import it.forgottenworld.dungeons.core.game.dungeon.DungeonManager
import it.forgottenworld.dungeons.core.utils.sendPrefixedMessage
import org.bukkit.entity.Player

class CmdDungeonPoints @Inject constructor(
    private val dungeonManager: DungeonManager
) : PlayerCommand() {

    override fun command(sender: Player, args: Array<out String>): Boolean {
        if (args.isEmpty()) {
            sender.sendPrefixedMessage(Strings.NEA_PLEASE_PROVIDE_AMOUNT)
            return true
        }

        val points = args[0].toIntOrNull() ?: run {
            sender.sendPrefixedMessage(Strings.INVALID_ARG_AMOUNT_OF_POINTS_SHOULD_BE_INT)
            return true
        }

        val dungeon = dungeonManager.getPlayerEditableDungeon(sender.uniqueId) ?: run {
            sender.sendPrefixedMessage(Strings.NOT_EDITING_ANY_DUNGEONS)
            return true
        }

        dungeon.points = points
        sender.sendPrefixedMessage(Strings.DUNGEON_POINTS_CHANGED)

        return true
    }
}