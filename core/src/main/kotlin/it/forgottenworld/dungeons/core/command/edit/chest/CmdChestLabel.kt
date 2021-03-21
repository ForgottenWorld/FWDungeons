package it.forgottenworld.dungeons.core.command.edit.chest

import com.google.inject.Inject
import it.forgottenworld.dungeons.api.command.PlayerCommand
import it.forgottenworld.dungeons.api.game.dungeon.DungeonManager
import it.forgottenworld.dungeons.core.storage.Strings
import it.forgottenworld.dungeons.core.utils.sendPrefixedMessage
import org.bukkit.entity.Player

class CmdChestLabel @Inject constructor(
    private val dungeonManager: DungeonManager
) : PlayerCommand() {

    override fun command(sender: Player, args: Array<out String>): Boolean {
        val dungeon = dungeonManager.getPlayerEditableDungeon(sender.uniqueId) ?: run {
            sender.sendPrefixedMessage(Strings.NOT_EDITING_ANY_DUNGEONS)
            return true
        }

        if (args.isEmpty()) {
            sender.sendPrefixedMessage(Strings.LABEL_CANNOT_BE_EMPTY)
            return true
        }

        val firstArg = args[0]
        val chest = if (firstArg.startsWith("id:")) {
            if (args.size == 1) {
                sender.sendPrefixedMessage(Strings.LABEL_CANNOT_BE_EMPTY)
                return true
            }
            val id = firstArg.removePrefix("id:").toIntOrNull() ?: run {
                sender.sendPrefixedMessage(Strings.NO_CHEST_WITH_SUCH_ID)
                return true
            }
            dungeon.chests[id]
            return true
        } else dungeon.chests.values.lastOrNull() ?: run {
            sender.sendPrefixedMessage(Strings.NO_CHESTS_YET)
            return true
        }

        val label = args.joinToString(" ").trim()
        chest.label = label
        sender.sendPrefixedMessage(Strings.SET_LABEL, label)
        return true
    }
}