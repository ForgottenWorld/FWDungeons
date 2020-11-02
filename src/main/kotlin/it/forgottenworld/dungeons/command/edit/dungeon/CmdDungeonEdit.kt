package it.forgottenworld.dungeons.command.edit.dungeon

import it.forgottenworld.dungeons.model.dungeon.EditableDungeon.Companion.editableDungeon
import it.forgottenworld.dungeons.model.dungeon.FinalDungeon
import it.forgottenworld.dungeons.utils.ktx.sendFWDMessage
import org.bukkit.entity.Player

fun cmdDungeonEdit(sender: Player, args: Array<out String>): Boolean {
    if (args.isEmpty()) {
        sender.sendFWDMessage("Not enough arguments: please provide a dungeon id")
        return true
    }

    val id = args[0].toIntOrNull()
    if (id == null) {
        sender.sendFWDMessage("Dungeon id should be an integer")
        return true
    }

    if (sender.editableDungeon != null) {
        sender.sendFWDMessage("You're already editing a dungeon")
        return true
    }

    val dungeon = FinalDungeon.dungeons[id] ?: run {
        sender.sendFWDMessage("No dungeon found with id $id")
        return true
    }

    dungeon.putInEditMode(sender)

    return true
}