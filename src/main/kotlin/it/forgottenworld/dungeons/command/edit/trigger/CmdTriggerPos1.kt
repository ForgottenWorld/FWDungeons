package it.forgottenworld.dungeons.command.edit.trigger

import it.forgottenworld.dungeons.command.edit.helpers.InteractiveElementCommandHelper
import it.forgottenworld.dungeons.model.interactiveelement.InteractiveElementType
import org.bukkit.entity.Player

fun cmdTriggerPos1(sender: Player, args: Array<out String>): Boolean {
    InteractiveElementCommandHelper.setInteractiveElementPos(sender, 1, InteractiveElementType.TRIGGER)
    return true
}