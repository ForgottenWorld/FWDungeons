package it.forgottenworld.dungeons.command.edit.trigger

import it.forgottenworld.dungeons.command.edit.helpers.InteractiveElementCommandHelper
import it.forgottenworld.dungeons.model.interactiveelement.InteractiveElementType
import org.bukkit.entity.Player

fun cmdTriggerUnmake(sender: Player, args: Array<out String>): Boolean {
    InteractiveElementCommandHelper.unMakeInteractiveElement(sender, InteractiveElementType.TRIGGER)
    return true
}