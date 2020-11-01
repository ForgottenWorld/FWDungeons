package it.forgottenworld.dungeons.command.edit.activearea

import it.forgottenworld.dungeons.command.edit.helpers.InteractiveElementCommandHelper
import it.forgottenworld.dungeons.model.interactiveelement.InteractiveElementType
import org.bukkit.entity.Player

fun cmdActiveAreaPos1(sender: Player, args: Array<out String>): Boolean {
    InteractiveElementCommandHelper.setInteractiveElementPos(sender, 1, InteractiveElementType.ACTIVE_AREA)
    return true
}