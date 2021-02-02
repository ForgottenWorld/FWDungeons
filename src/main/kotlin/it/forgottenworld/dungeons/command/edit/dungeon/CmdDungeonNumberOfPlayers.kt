package it.forgottenworld.dungeons.command.edit.dungeon

import it.forgottenworld.dungeons.config.Strings
import it.forgottenworld.dungeons.model.dungeon.EditableDungeon.Companion.editableDungeon
import it.forgottenworld.dungeons.utils.sendFWDMessage
import org.bukkit.entity.Player

fun cmdDungeonNumberOfPlayers(sender: Player, args: Array<out String>): Boolean {
    if (args.count() < 2) {
        sender.sendFWDMessage(Strings.NEA_PROVIDE_MIN_MAX_PLAYERS)
        return true
    }

    val r1 = args[0].toIntOrNull()
    val r2 = args[1].toIntOrNull()
    if (r1 == null || r2 == null) {
        sender.sendFWDMessage(Strings.MIN_MAX_PLAYERS_SHOULD_BE_INT)
        return true
    }

    val dungeon = sender.editableDungeon ?: run {
        sender.sendFWDMessage(Strings.NOT_EDITING_ANY_DUNGEONS)
        return true
    }

    dungeon.numberOfPlayers = r1..r2
    sender.sendFWDMessage(Strings.NUMBER_OF_PLAYERS_CHANGED)

    return true
}