package it.forgottenworld.dungeons.command.edit.trigger

import it.forgottenworld.dungeons.manager.InteractiveElementManager
import it.forgottenworld.dungeons.model.interactiveelement.InteractiveElementType
import org.bukkit.entity.Player

fun cmdTriggerPos1(sender: Player, args: Array<out String>): Boolean {
    InteractiveElementManager.setInteractiveElementPos(sender, 1, InteractiveElementType.TRIGGER)
    return true
}