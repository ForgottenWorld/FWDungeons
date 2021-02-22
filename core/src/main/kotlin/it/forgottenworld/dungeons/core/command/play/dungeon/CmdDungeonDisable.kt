package it.forgottenworld.dungeons.core.command.play.dungeon

import it.forgottenworld.dungeons.api.command.SenderCommand
import it.forgottenworld.dungeons.core.config.Strings
import it.forgottenworld.dungeons.core.game.dungeon.DungeonManager
import it.forgottenworld.dungeons.core.game.dungeon.DungeonManager.instances
import it.forgottenworld.dungeons.core.utils.sendFWDMessage
import org.bukkit.command.CommandSender

class CmdDungeonDisable : SenderCommand() {

    override fun command(sender: CommandSender, args: Array<out String>): Boolean {
        if (args.count() < 1) {
            sender.sendFWDMessage(Strings.PROVIDE_DUNGEON_ID)
            return true
        }

        val dungeonId = args[0].toIntOrNull()

        if (dungeonId == null) {
            sender.sendFWDMessage(Strings.DUNGEON_ID_SHOULD_BE_INT)
            return true
        }

        val res = DungeonManager.finalDungeons[dungeonId]?.let { d ->
            d.instances.values.forEach { it.evacuate() }
            d.isActive = false
        } != null

        sender.sendFWDMessage(
            if (res) {
                Strings.DUNGEON_WITH_ID_WAS_DISABLED.format(dungeonId)
            } else {
                Strings.NO_DUNGEON_FOUND_WITH_ID.format(dungeonId)
            }
        )

        return true
    }
}