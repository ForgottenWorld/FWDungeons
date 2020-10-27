package it.forgottenworld.dungeons.command.edit.trigger

import it.forgottenworld.dungeons.service.InteractiveElementService
import it.forgottenworld.dungeons.model.interactiveelement.InteractiveElementType
import org.bukkit.entity.Player

fun cmdTriggerLabel(sender: Player, args: Array<out String>): Boolean {
    val label = args.joinToString(" ").trim()
    InteractiveElementService.labelInteractiveElement(sender, label, InteractiveElementType.TRIGGER)
    return true
}