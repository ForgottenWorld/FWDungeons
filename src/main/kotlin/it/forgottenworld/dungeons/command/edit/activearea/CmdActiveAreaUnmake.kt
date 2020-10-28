package it.forgottenworld.dungeons.command.edit.activearea

import it.forgottenworld.dungeons.manager.InteractiveElementManager
import it.forgottenworld.dungeons.model.interactiveelement.InteractiveElementType
import org.bukkit.entity.Player

fun cmdActiveAreaUnmake(sender: Player, args: Array<out String>): Boolean {
    InteractiveElementManager.unMakeInteractiveElement(sender, InteractiveElementType.TRIGGER)
    return true
}