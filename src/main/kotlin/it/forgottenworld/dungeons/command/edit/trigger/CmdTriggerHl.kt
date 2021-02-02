package it.forgottenworld.dungeons.command.edit.trigger

import it.forgottenworld.dungeons.command.edit.helpers.InteractiveElementCommandHelper
import it.forgottenworld.dungeons.config.Strings
import it.forgottenworld.dungeons.model.interactiveelement.InteractiveElementType
import it.forgottenworld.dungeons.utils.sendFWDMessage
import org.bukkit.entity.Player

fun cmdTriggerHl(sender: Player, args: Array<out String>): Boolean {
    val id = args.getOrNull(0)?.toIntOrNull() ?: run {
        sender.sendFWDMessage(Strings.PROVIDE_VALID_TRIGGER_ID)
        return true
    }

    InteractiveElementCommandHelper.highlightInteractiveElement(sender, InteractiveElementType.TRIGGER, id)
    return true
}