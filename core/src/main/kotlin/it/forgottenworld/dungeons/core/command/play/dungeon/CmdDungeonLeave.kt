package it.forgottenworld.dungeons.core.command.play.dungeon

import com.google.inject.Inject
import it.forgottenworld.dungeons.api.command.PlayerCommand
import it.forgottenworld.dungeons.core.config.Strings
import it.forgottenworld.dungeons.core.game.dungeon.DungeonManager
import it.forgottenworld.dungeons.core.utils.sendPrefixedMessage
import org.bukkit.entity.Player

class CmdDungeonLeave @Inject constructor(
    private val dungeonManager: DungeonManager
) : PlayerCommand() {

    override fun command(sender: Player, args: Array<out String>): Boolean {
        dungeonManager.getPlayerInstance(sender.uniqueId)?.let {
            if (it.isInGame) {
                sender.sendPrefixedMessage(Strings.INSTANCE_HAS_STARTED_CANT_LEAVE_NOW)
                return true
            }
            it.onPlayerLeave(sender)
        } ?: sender.sendPrefixedMessage(Strings.CURRENTLY_NOT_IN_DUNGEON_PARTY)

        return true
    }
}