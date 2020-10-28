package it.forgottenworld.dungeons.command.edit.activearea

import it.forgottenworld.dungeons.manager.InteractiveElementManager
import it.forgottenworld.dungeons.model.interactiveelement.InteractiveElementType
import org.bukkit.entity.Player

fun cmdActiveAreaWand(sender: Player, args: Array<out String>): Boolean {
    InteractiveElementManager.grantWandForInteractiveElement(sender, InteractiveElementType.ACTIVE_AREA)
    return true
}