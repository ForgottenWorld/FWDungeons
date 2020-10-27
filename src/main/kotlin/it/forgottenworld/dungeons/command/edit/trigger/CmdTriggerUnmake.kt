package it.forgottenworld.dungeons.command.edit.trigger

import it.forgottenworld.dungeons.service.InteractiveElementService
import it.forgottenworld.dungeons.model.interactiveelement.InteractiveElementType
import org.bukkit.entity.Player

fun cmdTriggerUnmake(sender: Player, args: Array<out String>): Boolean {
    InteractiveElementService.unMakeInteractiveElement(sender, InteractiveElementType.TRIGGER)
    return true
}