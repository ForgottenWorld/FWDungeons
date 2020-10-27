package it.forgottenworld.dungeons.command.edit.activearea

import it.forgottenworld.dungeons.service.InteractiveElementService
import it.forgottenworld.dungeons.model.interactiveelement.InteractiveElementType
import org.bukkit.entity.Player

fun cmdActiveAreaWand(sender: Player, args: Array<out String>): Boolean {
    InteractiveElementService.grantWandForInteractiveElement(sender, InteractiveElementType.ACTIVE_AREA)
    return true
}