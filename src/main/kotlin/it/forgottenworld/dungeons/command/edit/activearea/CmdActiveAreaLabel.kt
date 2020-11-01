package it.forgottenworld.dungeons.command.edit.activearea

import it.forgottenworld.dungeons.command.edit.helpers.InteractiveElementCommandHelper
import it.forgottenworld.dungeons.model.interactiveelement.InteractiveElementType
import org.bukkit.entity.Player

fun cmdActiveAreaLabel(sender: Player, args: Array<out String>): Boolean {
    val label = args.joinToString(" ").trim()
    InteractiveElementCommandHelper.labelInteractiveElement(sender, label, InteractiveElementType.ACTIVE_AREA)
    return true
}