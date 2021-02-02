package it.forgottenworld.dungeons.command.edit.dungeon

import it.forgottenworld.dungeons.config.Strings
import it.forgottenworld.dungeons.model.dungeon.EditableDungeon.Companion.editableDungeon
import it.forgottenworld.dungeons.utils.sendFWDMessage
import org.bukkit.entity.Player

fun cmdDungeonDescription(sender: Player, args: Array<out String>): Boolean {
    if (args.isEmpty()) {
        sender.sendFWDMessage(Strings.NEA_PROVIDE_DESCRIPTION)
        return true
    }

    val dungeon = sender.editableDungeon ?: run {
        sender.sendFWDMessage(Strings.NOT_EDITING_ANY_DUNGEONS)
        return true
    }

    dungeon.description = args.joinToString(" ")
    sender.sendFWDMessage(Strings.DUNGEON_DESCRIPTION_CHANGED)

    return true
}