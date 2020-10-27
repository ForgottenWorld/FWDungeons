package it.forgottenworld.dungeons.command.edit.activearea

import it.forgottenworld.dungeons.service.InteractiveElementService
import it.forgottenworld.dungeons.model.interactiveelement.InteractiveElementType
import org.bukkit.entity.Player

fun cmdActiveAreaUnmake(sender: Player, args: Array<out String>): Boolean {
    InteractiveElementService.unMakeInteractiveElement(sender, InteractiveElementType.TRIGGER)
    return true
}