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

    val res = FinalDungeon.dungeons[id]?.let { d ->
        when {
            d.active -> false
            else -> {
                d.putInEditMode(sender)!!
                true
            }
        }
    } == true

    sender.sendFWDMessage(
            if (res) "Now editing dungeon with id $id"
            else "No currently disabled dungeons found with id $id"
    )

    return true
}