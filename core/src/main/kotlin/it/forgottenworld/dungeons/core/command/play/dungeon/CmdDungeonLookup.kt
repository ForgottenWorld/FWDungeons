package it.forgottenworld.dungeons.core.command.play.dungeon

import com.google.inject.Inject
import it.forgottenworld.dungeons.api.command.SenderCommand
import it.forgottenworld.dungeons.core.config.Strings
import it.forgottenworld.dungeons.core.game.dungeon.DungeonManager
import it.forgottenworld.dungeons.core.utils.sendFWDMessage
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender

class CmdDungeonLookup @Inject constructor(
    private val dungeonManager: DungeonManager
) : SenderCommand() {

    override fun command(sender: CommandSender, args: Array<out String>): Boolean {
        if (args.count() == 0) {
            sender.sendFWDMessage(Strings.PROVIDE_PLAYER_NAME)
            return true
        }

        val player = Bukkit.getPlayer(args[0])
            ?: run {
                sender.sendFWDMessage(Strings.PLAYER_NOT_FOUND)
                return true
            }

        val instance = dungeonManager.getPlayerInstance(player.uniqueId)
            ?: run {
                sender.sendFWDMessage(Strings.PLAYER_IS_NOT_IN_PARTY_OR_INSTANCE)
                return true
            }

        sender.sendFWDMessage(Strings.LOOKUP_RESULT.format(args[0], instance.dungeon.id, instance.id))

        return true
    }
}