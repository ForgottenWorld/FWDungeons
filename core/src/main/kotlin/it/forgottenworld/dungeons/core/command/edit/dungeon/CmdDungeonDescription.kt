package it.forgottenworld.dungeons.core.command.edit.dungeon

import it.forgottenworld.dungeons.core.config.Strings
import it.forgottenworld.dungeons.core.game.DungeonManager.editableDungeon
import it.forgottenworld.dungeons.core.utils.sendFWDMessage
import it.forgottenworld.dungeons.api.command.PlayerCommand
import org.bukkit.entity.Player

class CmdDungeonDescription : PlayerCommand() {

    override fun command(sender: Player, args: Array<out String>): Boolean {
        if (args.isEmpty()) {
            sender.sendFWDMessage(Strings.NEA_PROVIDE_DESCRIPTION)
            return true
        }

        val dungeon = sender.uniqueId.editableDungeon ?: run {
            sender.sendFWDMessage(Strings.NOT_EDITING_ANY_DUNGEONS)
            return true
        }

        dungeon.description = args.joinToString(" ")
        sender.sendFWDMessage(Strings.DUNGEON_DESCRIPTION_CHANGED)

        return true
    }
}