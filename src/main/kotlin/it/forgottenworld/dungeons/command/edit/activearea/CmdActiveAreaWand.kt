package it.forgottenworld.dungeons.command.edit.activearea

import it.forgottenworld.dungeons.command.edit.helpers.InteractiveElementCommandHelper
import it.forgottenworld.dungeons.model.interactiveelement.InteractiveElementType
import org.bukkit.entity.Player

fun cmdActiveAreaWand(sender: Player, args: Array<out String>): Boolean {
    InteractiveElementCommandHelper.grantWandForInteractiveElement(sender, InteractiveElementType.ACTIVE_AREA)
    return true
}