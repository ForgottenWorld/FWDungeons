package it.forgottenworld.dungeons.core.command.play.dungeon

import com.google.inject.Inject
import it.forgottenworld.dungeons.api.command.PlayerCommand
import it.forgottenworld.dungeons.core.storage.Strings
import it.forgottenworld.dungeons.api.game.dungeon.DungeonManager
import it.forgottenworld.dungeons.core.utils.sendPrefixedMessage
import org.bukkit.entity.Player

class CmdDungeonStart @Inject constructor(
    private val dungeonManager: DungeonManager
) : PlayerCommand() {

    override fun command(sender: Player, args: Array<out String>): Boolean {
        val instance = dungeonManager.getPlayerInstance(sender.uniqueId) ?: run {
            sender.sendPrefixedMessage(Strings.CURRENTLY_NOT_IN_DUNGEON_PARTY)
            return true
        }

        when {
            instance.leader != sender.uniqueId -> {
                sender.sendPrefixedMessage(Strings.ONLY_LEADER_MAY_START_INSTANCE)
            }
            instance.players.size < instance.dungeon.minPlayers -> {
                sender.sendPrefixedMessage(Strings.NOT_ENOUGH_PLAYERS_FOR_DUNGEON)
            }
            else -> {
                dungeonManager.getPlayerInstance(sender.uniqueId)?.onStart()
                sender.sendPrefixedMessage(Strings.DUNGEON_PARTY_MEMBERS_HAVE_BEEN_TPED)
            }
        }

        return true
    }
}