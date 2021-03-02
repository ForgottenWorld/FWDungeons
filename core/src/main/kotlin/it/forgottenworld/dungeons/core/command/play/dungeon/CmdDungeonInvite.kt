package it.forgottenworld.dungeons.core.command.play.dungeon

import com.google.inject.Inject
import it.forgottenworld.dungeons.api.command.PlayerCommand
import it.forgottenworld.dungeons.core.cli.JsonMessageGenerator
import it.forgottenworld.dungeons.core.config.Strings
import it.forgottenworld.dungeons.core.game.DungeonManager
import it.forgottenworld.dungeons.core.utils.sendFWDMessage
import it.forgottenworld.dungeons.core.utils.sendJsonMessage
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class CmdDungeonInvite @Inject constructor(
    private val jsonMessageGenerator: JsonMessageGenerator,
    private val dungeonManager: DungeonManager
) : PlayerCommand() {

    override fun command(sender: Player, args: Array<out String>): Boolean {
        if (args.isEmpty()) {
            sender.sendFWDMessage(Strings.PROVIDE_NAME_OF_INVITEE)
            return true
        }

        val instance = dungeonManager.getPlayerInstance(sender.uniqueId) ?: run {
            sender.sendFWDMessage(Strings.CURRENTLY_NOT_IN_DUNGEON_PARTY)
            return true
        }

        if (instance.leader != sender.uniqueId) {
            sender.sendFWDMessage(Strings.ONLY_LEADER_MAY_INVITE)
            return true
        }

        val toPlayer = Bukkit.getServer().getPlayer(args[0]) ?: run {
            sender.sendFWDMessage(Strings.NO_ONLINE_PLAYER_HAS_THIS_NAME)
            return true
        }

        toPlayer.sendJsonMessage(
            jsonMessageGenerator.invitation(
                sender.name,
                instance.dungeon.id,
                instance.id,
                instance.partyKey
            )
        )

        sender.sendFWDMessage(Strings.INVITE_SENT)

        return true
    }
}