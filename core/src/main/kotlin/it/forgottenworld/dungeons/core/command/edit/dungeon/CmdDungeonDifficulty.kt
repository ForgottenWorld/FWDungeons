package it.forgottenworld.dungeons.core.command.edit.dungeon

import com.google.inject.Inject
import it.forgottenworld.dungeons.api.command.PlayerCommand
import it.forgottenworld.dungeons.api.game.dungeon.Dungeon
import it.forgottenworld.dungeons.core.config.Strings
import it.forgottenworld.dungeons.core.game.DungeonManager
import it.forgottenworld.dungeons.core.utils.sendFWDMessage
import org.bukkit.entity.Player

class CmdDungeonDifficulty @Inject constructor(
    private val dungeonManager: DungeonManager
) : PlayerCommand() {

    override fun command(sender: Player, args: Array<out String>): Boolean {
        if (args.isEmpty()) {
            sender.sendFWDMessage(Strings.NEA_PROVIDE_DIFFICULTY)
            return true
        }

        Dungeon.Difficulty.values().map { it.toString() }.let {
            if (!it.contains(args[0].toLowerCase())) {
                sender.sendFWDMessage(Strings.INVALID_ARG_POSSIBLE_ARGS.format(it.joinToString(", ")))
                return true
            }

            val dungeon = dungeonManager.getPlayerEditableDungeon(sender.uniqueId) ?: run {
                sender.sendFWDMessage(Strings.NOT_EDITING_ANY_DUNGEONS)
                return true
            }

            dungeon.difficulty = Dungeon.Difficulty.fromString(args[0].toLowerCase())!!
            sender.sendFWDMessage(Strings.DUNGEON_DIFFICULTY_CHANGED)

        }

        return true
    }
}