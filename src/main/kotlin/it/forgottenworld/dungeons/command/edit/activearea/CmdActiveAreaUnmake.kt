package it.forgottenworld.dungeons.command.edit.activearea

import it.forgottenworld.dungeons.command.edit.helpers.InteractiveElementCommandHelper
import it.forgottenworld.dungeons.model.interactiveelement.InteractiveElementType
import org.bukkit.entity.Player

fun cmdActiveAreaUnmake(sender: Player, args: Array<out String>): Boolean {
    InteractiveElementCommandHelper.unMakeInteractiveElement(
        sender,
        InteractiveElementType.ACTIVE_AREA,
        args.getOrNull(0)?.toIntOrNull()
    )
    return true
}