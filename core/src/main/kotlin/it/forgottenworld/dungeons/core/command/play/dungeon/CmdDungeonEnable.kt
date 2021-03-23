package it.forgottenworld.dungeons.core.command.play.dungeon

import com.google.inject.Inject
import it.forgottenworld.dungeons.api.command.SenderCommand
import it.forgottenworld.dungeons.api.game.dungeon.DungeonManager
import it.forgottenworld.dungeons.core.storage.Strings
import it.forgottenworld.dungeons.core.utils.sendPrefixedMessage
import org.bukkit.command.CommandSender

class CmdDungeonEnable @Inject constructor(
    private val dungeonManager: DungeonManager
) : SenderCommand() {

    override fun command(sender: CommandSender, args: Array<out String>): Boolean {
        if (args.count() < 1) {
            sender.sendPrefixedMessage(Strings.PROVIDE_DUNGEON_ID)
            return true
        }

        val id = args[0].toIntOrNull() ?: run {
            sender.sendPrefixedMessage(Strings.DUNGEON_ID_SHOULD_BE_INT)
            return true
        }

        val dungeon = dungeonManager.getFinalDungeonById(id) ?: run {
            sender.sendPrefixedMessage(Strings.NO_DUNGEON_FOUND_WITH_ID, id)
            return true
        }

        if (dungeon.isBeingEdited) {
            sender.sendPrefixedMessage(Strings.DUNGEON_WITH_ID_IS_BEING_EDITED, id)
            return true
        }

        if (dungeon.isActive) {
            sender.sendPrefixedMessage(Strings.DUNGEON_WITH_ID_ALREADY_ACTIVE, id)
            return true
        }

        if (dungeonManager.getDungeonInstances(dungeon).isEmpty()) {
            sender.sendPrefixedMessage(Strings.DUNGEON_WITH_ID_HAS_NO_INSTANCES_IMPORT_IT, id, id)
            return true
        }

        dungeonManager.enableDungeon(id)
        sender.sendPrefixedMessage(Strings.DUNGEON_WITH_ID_WAS_ENABLED, id)

        return true
    }
}