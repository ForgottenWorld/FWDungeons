package it.forgottenworld.dungeons.command.edit.activearea

import it.forgottenworld.dungeons.command.edit.helpers.InteractiveElementCommandHelper
import it.forgottenworld.dungeons.config.Strings
import it.forgottenworld.dungeons.model.interactiveelement.InteractiveElementType
import it.forgottenworld.dungeons.utils.ktx.sendFWDMessage
import org.bukkit.entity.Player

fun cmdActiveAreaHl(sender: Player, args: Array<out String>): Boolean {
    val id = args.getOrNull(0)?.toIntOrNull() ?: run {
        sender.sendFWDMessage(Strings.PROVIDE_VALID_ACTIVE_AREA_ID)
        return true
    }

    InteractiveElementCommandHelper.highlightInteractiveElement(sender, InteractiveElementType.ACTIVE_AREA, id)
    return true
}