package it.forgottenworld.dungeons.command.edit.activearea

import it.forgottenworld.dungeons.model.interactiveelement.InteractiveElementType
import it.forgottenworld.dungeons.manager.InteractiveElementManager
import org.bukkit.entity.Player

fun cmdActiveAreaLabel(sender: Player, args: Array<out String>): Boolean {
    val label = args.joinToString(" ").trim()
    InteractiveElementManager.labelInteractiveElement(sender, label, InteractiveElementType.ACTIVE_AREA)
    return true
}