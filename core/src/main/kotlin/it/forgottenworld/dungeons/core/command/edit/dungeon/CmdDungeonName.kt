package it.forgottenworld.dungeons.core.command.edit.dungeon

import com.google.inject.Inject
import it.forgottenworld.dungeons.api.command.PlayerCommand
import it.forgottenworld.dungeons.core.storage.Strings
import it.forgottenworld.dungeons.api.game.dungeon.DungeonManager
import it.forgottenworld.dungeons.core.utils.sendPrefixedMessage
import org.bukkit.entity.Player

class CmdDungeonName @Inject constructor(
    private val dungeonManager: DungeonManager
) : PlayerCommand() {

    override fun command(sender: Player, args: Array<out String>): Boolean {
        if (args.isEmpty()) {
            sender.sendPrefixedMessage(Strings.NEA_PROVIDE_NAME)
            return true
        }

        val name = args.joinToString(" ")

        val dungeon = dungeonManager.getPlayerEditableDungeon(sender.uniqueId) ?: run {
            sender.sendPrefixedMessage(Strings.NOT_EDITING_ANY_DUNGEONS)
            return true
        }

        if (dungeonManager.getAllFinalDungeons().any { it.name.equals(name.trim(), true) }) {
            sender.sendPrefixedMessage(Strings.ANOTHER_DUNGEON_WITH_SAME_NAME_EXISTS)
            return true
        }

        dungeon.name = name
        sender.sendPrefixedMessage(Strings.DUNGEON_NAME_CHANGED)

        return true
    }
}