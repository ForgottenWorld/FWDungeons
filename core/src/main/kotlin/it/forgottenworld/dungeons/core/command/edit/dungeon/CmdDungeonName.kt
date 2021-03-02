package it.forgottenworld.dungeons.core.command.edit.dungeon

import com.google.inject.Inject
import it.forgottenworld.dungeons.api.command.PlayerCommand
import it.forgottenworld.dungeons.core.config.Strings
import it.forgottenworld.dungeons.core.game.DungeonManager
import it.forgottenworld.dungeons.core.utils.sendFWDMessage
import org.bukkit.entity.Player

class CmdDungeonName @Inject constructor(
    private val dungeonManager: DungeonManager
) : PlayerCommand() {

    override fun command(sender: Player, args: Array<out String>): Boolean {
        if (args.isEmpty()) {
            sender.sendFWDMessage(Strings.NEA_PROVIDE_NAME)
            return true
        }

        val name = args.joinToString(" ")

        val dungeon = dungeonManager.getPlayerEditableDungeon(sender.uniqueId) ?: run {
            sender.sendFWDMessage(Strings.NOT_EDITING_ANY_DUNGEONS)
            return true
        }

        if (dungeonManager.finalDungeons.values.any { it.name.equals(name.trim(), true) }) {
            sender.sendFWDMessage(Strings.ANOTHER_DUNGEON_WITH_SAME_NAME_EXISTS)
            return true
        }

        dungeon.name = name
        sender.sendFWDMessage(Strings.DUNGEON_NAME_CHANGED)

        return true
    }
}