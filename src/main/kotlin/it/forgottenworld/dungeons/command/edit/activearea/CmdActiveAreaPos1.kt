package it.forgottenworld.dungeons.command.edit.activearea

import it.forgottenworld.dungeons.manager.InteractiveElementManager
import it.forgottenworld.dungeons.model.interactiveelement.InteractiveElementType
import org.bukkit.entity.Player

fun cmdActiveAreaPos1(sender: Player, args: Array<out String>): Boolean {
    InteractiveElementManager.setInteractiveElementPos(sender, 1, InteractiveElementType.ACTIVE_AREA)
    return true
}