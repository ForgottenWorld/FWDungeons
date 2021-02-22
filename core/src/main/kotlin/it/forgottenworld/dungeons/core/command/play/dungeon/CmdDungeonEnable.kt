package it.forgottenworld.dungeons.core.command.play.dungeon

import it.forgottenworld.dungeons.api.command.SenderCommand
import it.forgottenworld.dungeons.core.config.Strings
import it.forgottenworld.dungeons.core.game.dungeon.DungeonManager
import it.forgottenworld.dungeons.core.game.dungeon.DungeonManager.instances
import it.forgottenworld.dungeons.core.utils.sendFWDMessage
import org.bukkit.command.CommandSender

class CmdDungeonEnable : SenderCommand() {

    override fun command(sender: CommandSender, args: Array<out String>): Boolean {
        if (args.count() < 1) {
            sender.sendFWDMessage(Strings.PROVIDE_DUNGEON_ID)
            return true
        }

        val id = args[0].toIntOrNull() ?: run {
            sender.sendFWDMessage(Strings.DUNGEON_ID_SHOULD_BE_INT)
            return true
        }

        val dungeon = DungeonManager.finalDungeons[id] ?: run {
            sender.sendFWDMessage(Strings.NO_DUNGEON_FOUND_WITH_ID.format(id))
            return true
        }

        if (dungeon.isBeingEdited) {
            sender.sendFWDMessage(Strings.DUNGEON_WITH_ID_IS_BEING_EDITED.format(id))
            return true
        }

        if (dungeon.isActive) {
            sender.sendFWDMessage(Strings.DUNGEON_WITH_ID_ALREADY_ACTIVE.format(id))
            return true
        }

        if (dungeon.instances.isEmpty()) {
            sender.sendFWDMessage(Strings.DUNGEON_WITH_ID_HAS_NO_INSTANCES_IMPORT_IT.format(id, id))
            return true
        }

        dungeon.isActive = true
        sender.sendFWDMessage(Strings.DUNGEON_WITH_ID_WAS_ENABLED.format(id))

        return true
    }
}