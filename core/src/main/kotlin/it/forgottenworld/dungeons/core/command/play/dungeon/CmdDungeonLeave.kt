package it.forgottenworld.dungeons.core.command.play.dungeon

import it.forgottenworld.dungeons.api.command.PlayerCommand
import it.forgottenworld.dungeons.core.config.Strings
import it.forgottenworld.dungeons.core.game.dungeon.DungeonManager.finalInstance
import it.forgottenworld.dungeons.core.utils.sendFWDMessage
import org.bukkit.entity.Player

class CmdDungeonLeave : PlayerCommand() {

    override fun command(sender: Player, args: Array<out String>): Boolean {
        sender.uniqueId.finalInstance?.let {
            if (it.inGame) {
                sender.sendFWDMessage(Strings.INSTANCE_HAS_STARTED_CANT_LEAVE_NOW)
                return true
            }
            it.onPlayerLeave(sender)
        } ?: sender.sendFWDMessage(Strings.CURRENTLY_NOT_IN_DUNGEON_PARTY)

        return true
    }
}