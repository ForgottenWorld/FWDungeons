package it.forgottenworld.dungeons.command.edit.activearea

import it.forgottenworld.dungeons.model.interactiveelement.InteractiveElementType
import it.forgottenworld.dungeons.service.InteractiveElementService
import org.bukkit.entity.Player

fun cmdActiveAreaLabel(sender: Player, args: Array<out String>): Boolean {
    val label = args.joinToString(" ").trim()
    InteractiveElementService.labelInteractiveElement(sender, label, InteractiveElementType.ACTIVE_AREA)
    return true
}