package it.forgottenworld.dungeons.command.edit.activearea

import it.forgottenworld.dungeons.command.edit.helpers.InteractiveElementCommandHelper
import it.forgottenworld.dungeons.model.interactiveelement.InteractiveElementType
import it.forgottenworld.dungeons.utils.ktx.sendFWDMessage
import org.bukkit.entity.Player

fun cmdActiveAreaHl(sender: Player, args: Array<out String>): Boolean {
    val id = args.getOrNull(0)?.toIntOrNull() ?: run {
        sender.sendFWDMessage("Please provide a valid active area id")
        return true
    }

    InteractiveElementCommandHelper.highlightInteractiveElement(sender, InteractiveElementType.ACTIVE_AREA, id)
    return true
}