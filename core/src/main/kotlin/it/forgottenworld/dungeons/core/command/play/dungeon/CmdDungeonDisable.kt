package it.forgottenworld.dungeons.core.command.play.dungeon

import com.google.inject.Inject
import it.forgottenworld.dungeons.api.command.SenderCommand
import it.forgottenworld.dungeons.api.game.dungeon.DungeonManager
import it.forgottenworld.dungeons.core.storage.Strings
import it.forgottenworld.dungeons.core.utils.sendPrefixedMessage
import org.bukkit.command.CommandSender

class CmdDungeonDisable @Inject constructor(
    private val dungeonManager: DungeonManager
) : SenderCommand() {

    override fun command(sender: CommandSender, args: Array<out String>): Boolean {
        if (args.count() < 1) {
            sender.sendPrefixedMessage(Strings.PROVIDE_DUNGEON_ID)
            return true
        }

        val dungeonId = args[0].toIntOrNull()

        if (dungeonId == null) {
            sender.sendPrefixedMessage(Strings.DUNGEON_ID_SHOULD_BE_INT)
            return true
        }

        val res = dungeonManager.disableDungeon(dungeonId)

        val message = if (res) {
            Strings.DUNGEON_WITH_ID_WAS_DISABLED
        } else {
            Strings.NO_DUNGEON_FOUND_WITH_ID
        }
        sender.sendPrefixedMessage(message, dungeonId)
        return true
    }
}