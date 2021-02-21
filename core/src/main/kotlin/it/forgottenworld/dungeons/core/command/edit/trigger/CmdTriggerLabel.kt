package it.forgottenworld.dungeons.core.command.edit.trigger

import it.forgottenworld.dungeons.api.command.PlayerCommand
import it.forgottenworld.dungeons.core.command.edit.helpers.InteractiveRegionCommandHelper
import it.forgottenworld.dungeons.api.game.interactiveregion.InteractiveRegion
import org.bukkit.entity.Player

class CmdTriggerLabel : PlayerCommand() {

    override fun command(sender: Player, args: Array<out String>): Boolean {
        if (args.isEmpty()) return true
        val firstArg = args[0]
        if (firstArg.startsWith("id:")) {
            val id = firstArg.removePrefix("id:").toIntOrNull() ?: return true
            val label = args.drop(1).joinToString(" ").trim()
            InteractiveRegionCommandHelper.labelInteractiveRegion(sender, label, InteractiveRegion.Type.TRIGGER, id)
            return true
        }
        val label = args.joinToString(" ").trim()
        InteractiveRegionCommandHelper.labelInteractiveRegion(sender, label, InteractiveRegion.Type.TRIGGER)
        return true
    }
}