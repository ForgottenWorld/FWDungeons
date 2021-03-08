package it.forgottenworld.dungeons.core.command.edit.dungeon

import com.google.inject.Inject
import it.forgottenworld.dungeons.api.command.PlayerCommand
import it.forgottenworld.dungeons.api.game.dungeon.Dungeon
import it.forgottenworld.dungeons.core.config.Strings
import it.forgottenworld.dungeons.api.game.dungeon.DungeonManager
import it.forgottenworld.dungeons.core.utils.sendPrefixedMessage
import org.bukkit.entity.Player

class CmdDungeonDifficulty @Inject constructor(
    private val dungeonManager: DungeonManager
) : PlayerCommand() {

    override fun command(sender: Player, args: Array<out String>): Boolean {
        if (args.isEmpty()) {
            sender.sendPrefixedMessage(Strings.NEA_PROVIDE_DIFFICULTY)
            return true
        }

        Dungeon.Difficulty.values().map { it.toString() }.let {
            if (!it.contains(args[0].toLowerCase())) {
                sender.sendPrefixedMessage(Strings.INVALID_ARG_POSSIBLE_ARGS.format(it.joinToString(", ")))
                return true
            }

            val dungeon = dungeonManager.getPlayerEditableDungeon(sender.uniqueId) ?: run {
                sender.sendPrefixedMessage(Strings.NOT_EDITING_ANY_DUNGEONS)
                return true
            }

            dungeon.difficulty = Dungeon.Difficulty.fromString(args[0].toLowerCase())!!
            sender.sendPrefixedMessage(Strings.DUNGEON_DIFFICULTY_CHANGED)

        }

        return true
    }
}