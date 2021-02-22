package it.forgottenworld.dungeons.core.command.edit.trigger

import it.forgottenworld.dungeons.api.command.PlayerCommand
import it.forgottenworld.dungeons.api.game.interactiveregion.InteractiveRegion
import it.forgottenworld.dungeons.core.command.edit.helpers.InteractiveRegionCommandHelper
import it.forgottenworld.dungeons.core.config.Strings
import it.forgottenworld.dungeons.core.utils.sendFWDMessage
import org.bukkit.entity.Player

class CmdTriggerLabel : PlayerCommand() {

    override fun command(sender: Player, args: Array<out String>): Boolean {
        val firstArg = args[0]
        if (firstArg.startsWith("id:")) {
            val id = firstArg.drop(3).toIntOrNull() ?: run {
                sender.sendFWDMessage(Strings.NO_TRIGGER_WITH_SUCH_ID)
                return true
            }
            if (args.size == 1) {
                sender.sendFWDMessage(Strings.LABEL_CANNOT_BE_EMPTY)
                return true
            }
            val label = args.drop(1).joinToString(" ").trim()
            InteractiveRegionCommandHelper.labelInteractiveRegion(sender, label, InteractiveRegion.Type.TRIGGER, id)
            return true
        }
        if (args.isEmpty()) {
            sender.sendFWDMessage(Strings.LABEL_CANNOT_BE_EMPTY)
            return true
        }
        val label = args.joinToString(" ").trim()
        InteractiveRegionCommandHelper.labelInteractiveRegion(sender, label, InteractiveRegion.Type.TRIGGER)
        return true
    }
}