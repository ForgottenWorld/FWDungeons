package it.forgottenworld.dungeons.core.command.edit.trigger

import it.forgottenworld.dungeons.api.command.PlayerCommand
import it.forgottenworld.dungeons.core.config.Strings
import it.forgottenworld.dungeons.core.game.DungeonManager.editableDungeon
import it.forgottenworld.dungeons.core.scripting.CodeParser
import it.forgottenworld.dungeons.core.utils.sendFWDMessage
import org.bukkit.entity.Player

class CmdTriggerCode : PlayerCommand() {

    override fun command(sender: Player, args: Array<out String>): Boolean {
        val dungeon = sender.uniqueId.editableDungeon ?: run {
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