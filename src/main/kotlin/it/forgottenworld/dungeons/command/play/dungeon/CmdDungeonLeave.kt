package it.forgottenworld.dungeons.command.play.dungeon

import it.forgottenworld.dungeons.command.api.CommandHandler
import it.forgottenworld.dungeons.config.Strings
import it.forgottenworld.dungeons.game.instance.DungeonFinalInstance.Companion.finalInstance
import it.forgottenworld.dungeons.utils.sendFWDMessage
import org.bukkit.entity.Player

class CmdDungeonLeave : CommandHandler<Player> {

    override fun command(sender: Player, args: Array<out String>): Boolean {
        sender.finalInstance?.let {
            if (it.inGame) {
                sender.sendFWDMessage(Strings.INSTANCE_HAS_STARTED_CANT_LEAVE_NOW)
                return true
            }
            it.onPlayerLeave(sender)
        } ?: sender.sendFWDMessage(Strings.CURRENTLY_NOT_IN_DUNGEON_PARTY)

        return true
    }
}