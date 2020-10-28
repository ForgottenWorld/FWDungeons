package it.forgottenworld.dungeons.command.edit.trigger

import it.forgottenworld.dungeons.manager.InteractiveElementManager
import it.forgottenworld.dungeons.model.interactiveelement.InteractiveElementType
import org.bukkit.entity.Player

fun cmdTriggerLabel(sender: Player, args: Array<out String>): Boolean {
    val label = args.joinToString(" ").trim()
    InteractiveElementManager.labelInteractiveElement(sender, label, InteractiveElementType.TRIGGER)
    return true
}