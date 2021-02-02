package it.forgottenworld.dungeons.command.edit.dungeon

import it.forgottenworld.dungeons.config.Strings
import it.forgottenworld.dungeons.model.dungeon.EditableDungeon.Companion.editableDungeon
import it.forgottenworld.dungeons.utils.sendFWDMessage
import org.bukkit.entity.Player

fun cmdDungeonPoints(sender: Player, args: Array<out String>): Boolean {
    if (args.isEmpty()) {
        sender.sendFWDMessage(Strings.NEA_PLEASE_PROVIDE_AMOUNT)
        return true
    }

    val points = args[0].toIntOrNull() ?: run {
        sender.sendFWDMessage(Strings.INVALID_ARG_AMOUNT_OF_POINTS_SHOULD_BE_INT)
        return true
    }

    val dungeon = sender.editableDungeon ?: run {
        sender.sendFWDMessage(Strings.NOT_EDITING_ANY_DUNGEONS)
        return true
    }

    dungeon.points = points
    sender.sendFWDMessage(Strings.DUNGEON_POINTS_CHANGED)

    return true
}