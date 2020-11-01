package it.forgottenworld.dungeons.command.edit.trigger

import it.forgottenworld.dungeons.command.edit.helpers.InteractiveElementCommandHelper
import it.forgottenworld.dungeons.model.interactiveelement.InteractiveElementType
import org.bukkit.entity.Player

fun cmdTriggerLabel(sender: Player, args: Array<out String>): Boolean {
    val label = args.joinToString(" ").trim()
    InteractiveElementCommandHelper.labelInteractiveElement(sender, label, InteractiveElementType.TRIGGER)
    return true
}