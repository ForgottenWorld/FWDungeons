package it.forgottenworld.dungeons.core.command.edit.dungeon

import com.google.inject.Inject
import it.forgottenworld.dungeons.api.command.PlayerCommand
import it.forgottenworld.dungeons.core.config.Strings
import it.forgottenworld.dungeons.core.game.DungeonManager
import it.forgottenworld.dungeons.core.utils.sendFWDMessage
import org.bukkit.entity.Player

class CmdDungeonDescription @Inject constructor(
    private val dungeonManager: DungeonManager
) : PlayerCommand() {

    override fun command(sender: Player, args: Array<out String>): Boolean {
        if (args.isEmpty()) {
            sender.sendFWDMessage(Strings.NEA_PROVIDE_DESCRIPTION)
            return true
        }

        val dungeon = dungeonManager.getPlayerEditableDungeon(sender.uniqueId) ?: run {
            sender.sendFWDMessage(Strings.NOT_EDITING_ANY_DUNGEONS)
            return true
        }

        dungeon.description = args.joinToString(" ")
        sender.sendFWDMessage(Strings.DUNGEON_DESCRIPTION_CHANGED)

        return true
    }
}