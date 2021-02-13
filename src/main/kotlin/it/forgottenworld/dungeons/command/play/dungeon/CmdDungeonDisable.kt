package it.forgottenworld.dungeons.command.play.dungeon

import it.forgottenworld.dungeons.command.api.CommandHandler
import it.forgottenworld.dungeons.config.Strings
import it.forgottenworld.dungeons.game.dungeon.FinalDungeon
import it.forgottenworld.dungeons.utils.sendFWDMessage
import org.bukkit.command.CommandSender

class CmdDungeonDisable : CommandHandler<CommandSender> {

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

        val res = FinalDungeon.dungeons[dungeonId]?.let { d ->
            d.instances.values.forEach { it.evacuate() }
            d.isActive = false
        } != null

        sender.sendFWDMessage(
            if (res)
                Strings.DUNGEON_WITH_ID_WAS_DISABLED.format(dungeonId)
            else
                Strings.NO_DUNGEON_FOUND_WITH_ID.format(dungeonId)
        )

        return true
    }
}