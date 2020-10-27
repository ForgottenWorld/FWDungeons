package it.forgottenworld.dungeons.command.edit.trigger

import it.forgottenworld.dungeons.service.InteractiveElementService
import it.forgottenworld.dungeons.model.interactiveelement.InteractiveElementType
import org.bukkit.entity.Player

fun cmdTriggerPos2(sender: Player, args: Array<out String>): Boolean {
    InteractiveElementService.setInteractiveElementPos(sender, 2, InteractiveElementType.TRIGGER)
    return true
}