package it.forgottenworld.dungeons.core.command.edit.chest

import com.google.inject.Inject
import it.forgottenworld.dungeons.api.command.PlayerCommand
import it.forgottenworld.dungeons.api.game.dungeon.DungeonManager
import it.forgottenworld.dungeons.core.config.Strings
import it.forgottenworld.dungeons.core.utils.sendPrefixedMessage
import org.bukkit.entity.Player

class CmdChestRemove @Inject constructor(
    private val dungeonManager: DungeonManager
) : PlayerCommand() {

    override fun command(sender: Player, args: Array<out String>): Boolean {
        val dungeon = dungeonManager.getPlayerEditableDungeon(sender.uniqueId) ?: run {
            sender.sendPrefixedMessage(Strings.NOT_EDITING_ANY_DUNGEONS)
            return true
        }

        val id = args.getOrNull(0)?.toIntOrNull()
            ?: dungeon.chests.keys.lastOrNull()
            ?: run {
                sender.sendPrefixedMessage(Strings.NO_CHESTS_YET)
                return true
            }

        if (!dungeon.chests.contains(id)) {
            sender.sendPrefixedMessage(Strings.NO_CHEST_WITH_SUCH_ID)
            return true
        }

        dungeon.chests.remove(id)
        sender.sendPrefixedMessage(Strings.CHEST_REMOVED_SUCCESFULLY, id)
        return true
    }
}