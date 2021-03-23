package it.forgottenworld.dungeons.core.command.play.dungeon

import com.google.inject.Inject
import it.forgottenworld.dungeons.api.command.SenderCommand
import it.forgottenworld.dungeons.api.game.dungeon.DungeonManager
import it.forgottenworld.dungeons.core.storage.Strings
import it.forgottenworld.dungeons.core.utils.sendPrefixedMessage
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender

class CmdDungeonLookup @Inject constructor(
    private val dungeonManager: DungeonManager
) : SenderCommand() {

    override fun command(sender: CommandSender, args: Array<out String>): Boolean {
        if (args.count() == 0) {
            sender.sendPrefixedMessage(Strings.PROVIDE_PLAYER_NAME)
            return true
        }

        val player = Bukkit.getPlayer(args[0])
            ?: run {
                sender.sendPrefixedMessage(Strings.PLAYER_NOT_FOUND)
                return true
            }

        val instance = dungeonManager.getPlayerInstance(player.uniqueId)
            ?: run {
                sender.sendPrefixedMessage(Strings.PLAYER_IS_NOT_IN_PARTY_OR_INSTANCE)
                return true
            }

        sender.sendPrefixedMessage(Strings.LOOKUP_RESULT, args[0], instance.dungeon.id, instance.id)

        return true
    }
}