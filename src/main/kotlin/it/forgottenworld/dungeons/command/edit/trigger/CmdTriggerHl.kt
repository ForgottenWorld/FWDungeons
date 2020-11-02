package it.forgottenworld.dungeons.command.edit.trigger

import it.forgottenworld.dungeons.command.edit.helpers.InteractiveElementCommandHelper
import it.forgottenworld.dungeons.model.interactiveelement.InteractiveElementType
import it.forgottenworld.dungeons.utils.ktx.sendFWDMessage
import org.bukkit.entity.Player

fun cmdTriggerHl(sender: Player, args: Array<out String>): Boolean {
    val id = args.getOrNull(0)?.toIntOrNull() ?: run {
        sender.sendFWDMessage("Please provide a valid trigger id")
        return true
    }

    InteractiveElementCommandHelper.highlightInteractiveElement(sender, InteractiveElementType.TRIGGER, id)
    return true
}