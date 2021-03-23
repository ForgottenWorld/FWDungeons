package it.forgottenworld.dungeons.core.command.edit.dungeon

import com.google.inject.Inject
import it.forgottenworld.dungeons.api.command.PlayerCommand
import it.forgottenworld.dungeons.api.game.dungeon.DungeonManager
import it.forgottenworld.dungeons.core.storage.Strings
import it.forgottenworld.dungeons.core.utils.sendPrefixedMessage
import org.bukkit.entity.Player

class CmdDungeonNumberOfPlayers @Inject constructor(
    private val dungeonManager: DungeonManager
) : PlayerCommand() {

    override fun command(sender: Player, args: Array<out String>): Boolean {
        if (args.count() < 2) {
            sender.sendPrefixedMessage(Strings.NEA_PROVIDE_MIN_MAX_PLAYERS)
            return true
        }

        val r1 = args[0].toIntOrNull()
        val r2 = args[1].toIntOrNull()
        if (r1 == null || r2 == null) {
            sender.sendPrefixedMessage(Strings.MIN_MAX_PLAYERS_SHOULD_BE_INT)
            return true
        }

        val dungeon = dungeonManager.getPlayerEditableDungeon(sender.uniqueId) ?: run {
            sender.sendPrefixedMessage(Strings.NOT_EDITING_ANY_DUNGEONS)
            return true
        }

        dungeon.minPlayers = r1
        dungeon.maxPlayers = r2
        sender.sendPrefixedMessage(Strings.NUMBER_OF_PLAYERS_CHANGED)

        return true
    }
}