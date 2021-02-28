package it.forgottenworld.dungeons.core.command.edit.dungeon

import it.forgottenworld.dungeons.api.command.PlayerCommand
import it.forgottenworld.dungeons.core.config.Strings
import it.forgottenworld.dungeons.core.game.dungeon.DungeonManager.editableDungeon
import it.forgottenworld.dungeons.core.utils.sendFWDMessage
import org.bukkit.entity.Player

class CmdDungeonInstanceRemove : PlayerCommand() {

    override fun command(sender: Player, args: Array<out String>): Boolean {
        val dungeon = sender.uniqueId.editableDungeon
            ?: run {
                sender.sendFWDMessage(Strings.NOT_EDITING_ANY_DUNGEONS)
                return true
            }

        if (dungeon.finalInstanceLocations.size < 2) {
            sender.sendFWDMessage(Strings.DUNGEONS_CANT_HAVE_LESS_THAN_1_INSTANCE)
            return true
        }

        val instToRemove = if (args.isEmpty()) {
            dungeon.finalInstanceLocations.lastIndex
        } else {
            args[0].toIntOrNull()
        }

        if (instToRemove == null) {
            sender.sendFWDMessage(Strings.PROVIDE_INDEX_OR_NO_INDEX_TO_REMOVE_LAST_INST)
            return true
        }

        dungeon.finalInstanceLocations.removeAt(instToRemove)

        sender.sendFWDMessage(Strings.REMOVED_INSTANCE_AT_INDEX.format(instToRemove))

        return true
    }
}