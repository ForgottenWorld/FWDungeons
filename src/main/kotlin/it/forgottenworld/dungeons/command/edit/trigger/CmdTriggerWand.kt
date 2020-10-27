package it.forgottenworld.dungeons.command.edit.trigger

import it.forgottenworld.dungeons.service.InteractiveElementService
import it.forgottenworld.dungeons.model.interactiveelement.InteractiveElementType
import org.bukkit.entity.Player

fun cmdTriggerWand(sender: Player, args: Array<out String>): Boolean {
    InteractiveElementService.grantWandForInteractiveElement(sender, InteractiveElementType.TRIGGER)
    return true
}