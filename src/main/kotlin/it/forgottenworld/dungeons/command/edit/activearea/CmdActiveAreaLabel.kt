package it.forgottenworld.dungeons.command.edit.activearea

import it.forgottenworld.dungeons.command.api.CommandHandler
import it.forgottenworld.dungeons.command.edit.helpers.InteractiveRegionCommandHelper
import it.forgottenworld.dungeons.game.interactiveregion.InteractiveRegion
import org.bukkit.entity.Player

class CmdActiveAreaLabel : CommandHandler<Player> {

    override fun command(sender: Player, args: Array<out String>): Boolean {
        if (args.isEmpty()) return true
        val firstArg = args[0]
        if (firstArg.startsWith("id:")) {
            val id = firstArg.removePrefix("id:").toIntOrNull() ?: return true
            val label = args.drop(1).joinToString(" ").trim()
            InteractiveRegionCommandHelper.labelInteractiveRegion(sender, label, InteractiveRegion.Type.ACTIVE_AREA, id)
            return true
        }
        val label = args.joinToString(" ").trim()
        InteractiveRegionCommandHelper.labelInteractiveRegion(sender, label, InteractiveRegion.Type.ACTIVE_AREA)
        return true
    }
}