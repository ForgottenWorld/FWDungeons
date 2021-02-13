package it.forgottenworld.dungeons.command.edit.trigger

import it.forgottenworld.dungeons.command.api.CommandHandler
import it.forgottenworld.dungeons.config.Strings
import it.forgottenworld.dungeons.game.dungeon.EditableDungeon.Companion.editableDungeon
import it.forgottenworld.dungeons.scripting.CodeParser
import it.forgottenworld.dungeons.utils.sendFWDMessage
import org.bukkit.entity.Player

class CmdTriggerCode : CommandHandler<Player> {

    override fun command(sender: Player, args: Array<out String>): Boolean {
        val dungeon = sender.editableDungeon ?: run {
            sender.sendFWDMessage(Strings.NOT_EDITING_ANY_DUNGEONS)
            return true
        }

        val triggerId = args.getOrNull(0)?.toIntOrNull() ?: run {
            sender.sendFWDMessage(Strings.PROVIDE_VALID_TRIGGER_ID)
            return true
        }

        val trigger = dungeon.triggers[triggerId] ?: run {
            sender.sendFWDMessage(Strings.TRIGGER_NOT_FOUND)
            return true
        }

        val code = CodeParser.beautify(trigger.effectCode)

        sender.sendFWDMessage("\n§7[ CODE ]")
        sender.sendMessage(code)

        return true
    }
}