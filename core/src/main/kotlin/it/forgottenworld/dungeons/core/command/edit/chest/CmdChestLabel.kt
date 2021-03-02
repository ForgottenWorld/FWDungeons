package it.forgottenworld.dungeons.core.command.edit.chest

import com.google.inject.Inject
import it.forgottenworld.dungeons.api.command.PlayerCommand
import it.forgottenworld.dungeons.core.config.Strings
import it.forgottenworld.dungeons.core.game.DungeonManager
import it.forgottenworld.dungeons.core.utils.sendFWDMessage
import org.bukkit.entity.Player

class CmdChestLabel @Inject constructor(
    private val dungeonManager: DungeonManager
) : PlayerCommand() {

    override fun command(sender: Player, args: Array<out String>): Boolean {
        val dungeon = dungeonManager.getPlayerEditableDungeon(sender.uniqueId) ?: run {
            sender.sendFWDMessage(Strings.NOT_EDITING_ANY_DUNGEONS)
            return true
        }

        if (args.isEmpty()) {
            sender.sendFWDMessage(Strings.LABEL_CANNOT_BE_EMPTY)
            return true
        }

        val firstArg = args[0]
        val chest = if (firstArg.startsWith("id:")) {
            if (args.size == 1) {
                sender.sendFWDMessage(Strings.LABEL_CANNOT_BE_EMPTY)
                return true
            }
            val id = firstArg.removePrefix("id:").toIntOrNull() ?: run {
                sender.sendFWDMessage(Strings.NO_CHEST_WITH_SUCH_ID)
                return true
            }
            dungeon.chests[id]
            return true
        } else dungeon.chests.values.lastOrNull() ?: run {
            sender.sendFWDMessage(Strings.NO_CHESTS_YET)
            return true
        }

        val label = args.joinToString(" ").trim()
        chest.label = label
        sender.sendFWDMessage(Strings.SET_LABEL.format(label))
        return true
    }
}