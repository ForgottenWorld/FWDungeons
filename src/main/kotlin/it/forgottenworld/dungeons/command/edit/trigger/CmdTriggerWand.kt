package it.forgottenworld.dungeons.command.edit.trigger

import it.forgottenworld.dungeons.command.edit.helpers.InteractiveElementCommandHelper
import it.forgottenworld.dungeons.model.interactiveelement.InteractiveElementType
import org.bukkit.entity.Player

fun cmdTriggerWand(sender: Player, args: Array<out String>): Boolean {
    InteractiveElementCommandHelper.grantWandForInteractiveElement(sender, InteractiveElementType.TRIGGER)
    return true
}