package it.forgottenworld.dungeons.command.edit.activearea

import it.forgottenworld.dungeons.cli.getInteractiveActiveAreaList
import it.forgottenworld.dungeons.model.dungeon.EditableDungeon.Companion.editableDungeon
import it.forgottenworld.dungeons.utils.ktx.sendFWDMessage
import org.bukkit.entity.Player

fun cmdActiveAreaList(sender: Player, args: Array<out String>): Boolean {
    val dungeon = sender.editableDungeon ?: run {
        sender.sendFWDMessage("You're not editing any dungeons")
        return true
    }

    sender.spigot().sendMessage(*getInteractiveActiveAreaList(dungeon, args.getOrNull(0)?.toIntOrNull() ?: 0))
    return true
}