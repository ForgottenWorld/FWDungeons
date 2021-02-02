package it.forgottenworld.dungeons.command.edit.dungeon

import it.forgottenworld.dungeons.config.Strings
import it.forgottenworld.dungeons.model.dungeon.EditableDungeon.Companion.editableDungeon
import it.forgottenworld.dungeons.model.dungeon.FinalDungeon
import it.forgottenworld.dungeons.utils.sendFWDMessage
import org.bukkit.entity.Player

fun cmdDungeonEdit(sender: Player, args: Array<out String>): Boolean {
    if (args.isEmpty()) {
        sender.sendFWDMessage(Strings.NEA_PROVIDE_DUNGEON_ID)
        return true
    }

    val id = args[0].toIntOrNull()
    if (id == null) {
        sender.sendFWDMessage(Strings.DUNGEON_ID_SHOULD_BE_INT)
        return true
    }

    if (sender.editableDungeon != null) {
        sender.sendFWDMessage(Strings.ALREADY_EDITING_DUNGEON)
        return true
    }

    val dungeon = FinalDungeon.dungeons[id] ?: run {
        sender.sendFWDMessage(Strings.NO_DUNGEON_FOUND_WITH_ID.format(id))
        return true
    }

    if (dungeon.instances.isEmpty()) {
        sender.sendFWDMessage(Strings.DUNGEON_WITH_ID_HAS_NO_INSTANCES_IMPORT_IT.format(id, id))
        return true
    }

    dungeon.putInEditMode(sender)

    return true
}