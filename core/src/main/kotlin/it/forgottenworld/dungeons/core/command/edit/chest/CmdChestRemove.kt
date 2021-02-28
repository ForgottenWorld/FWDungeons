package it.forgottenworld.dungeons.core.command.edit.chest

import it.forgottenworld.dungeons.api.command.PlayerCommand
import it.forgottenworld.dungeons.core.config.Strings
import it.forgottenworld.dungeons.core.game.dungeon.DungeonManager.editableDungeon
import it.forgottenworld.dungeons.core.utils.sendFWDMessage
import org.bukkit.entity.Player

class CmdChestRemove : PlayerCommand() {

    override fun command(sender: Player, args: Array<out String>): Boolean {
        val dungeon = sender.uniqueId.editableDungeon ?: run {
            sender.sendFWDMessage(Strings.NOT_EDITING_ANY_DUNGEONS)
            return true
        }

        val id = args.getOrNull(0)?.toIntOrNull()
            ?: dungeon.chests.keys.lastOrNull()
            ?: run {
                sender.sendFWDMessage(Strings.NO_CHESTS_YET)
                return true
            }

        if (!dungeon.chests.contains(id)) {
            sender.sendFWDMessage(Strings.NO_CHEST_WITH_SUCH_ID)
            return true
        }

        dungeon.chests.remove(id)
        sender.sendFWDMessage(Strings.CHEST_REMOVED_SUCCESFULLY.format(id))
        return true
    }
}