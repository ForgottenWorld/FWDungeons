package it.forgottenworld.dungeons.core.command.edit.dungeon

import com.google.inject.Inject
import it.forgottenworld.dungeons.api.command.PlayerCommand
import it.forgottenworld.dungeons.core.config.Strings
import it.forgottenworld.dungeons.api.game.dungeon.DungeonManager
import it.forgottenworld.dungeons.core.utils.sendPrefixedMessage
import org.bukkit.entity.Player

class CmdDungeonInstanceRemove @Inject constructor(
    private val dungeonManager: DungeonManager
) : PlayerCommand() {

    override fun command(sender: Player, args: Array<out String>): Boolean {
        val dungeon = dungeonManager.getPlayerEditableDungeon(sender.uniqueId)
            ?: run {
                sender.sendPrefixedMessage(Strings.NOT_EDITING_ANY_DUNGEONS)
                return true
            }

        if (dungeon.finalInstanceLocations.size < 2) {
            sender.sendPrefixedMessage(Strings.DUNGEONS_CANT_HAVE_LESS_THAN_1_INSTANCE)
            return true
        }

        val instToRemove = if (args.isEmpty()) {
            dungeon.finalInstanceLocations.lastIndex
        } else {
            args[0].toIntOrNull()
        }

        if (instToRemove == null) {
            sender.sendPrefixedMessage(Strings.PROVIDE_INDEX_OR_NO_INDEX_TO_REMOVE_LAST_INST)
            return true
        }

        dungeon.finalInstanceLocations.removeAt(instToRemove)

        sender.sendPrefixedMessage(Strings.REMOVED_INSTANCE_AT_INDEX, instToRemove)

        return true
    }
}