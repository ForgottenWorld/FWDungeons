package it.forgottenworld.dungeons.command.edit.trigger

import it.forgottenworld.dungeons.manager.InteractiveElementManager
import it.forgottenworld.dungeons.model.interactiveelement.InteractiveElementType
import org.bukkit.entity.Player

fun cmdTriggerWand(sender: Player, args: Array<out String>): Boolean {
    InteractiveElementManager.grantWandForInteractiveElement(sender, InteractiveElementType.TRIGGER)
    return true
}