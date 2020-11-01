package it.forgottenworld.dungeons.command.edit.dungeon

import it.forgottenworld.dungeons.model.dungeon.EditableDungeon.Companion.editableDungeon
import it.forgottenworld.dungeons.model.dungeon.FinalDungeon
import it.forgottenworld.dungeons.utils.ktx.sendFWDMessage
import org.bukkit.entity.Player

fun cmdDungeonName(sender: Player, args: Array<out String>): Boolean {
    if (args.isEmpty()) {
        sender.sendFWDMessage("Not enough arguments: please provide a name")
        return true
    }

    val name = args.joinToString(" ")

    val dungeon = sender.editableDungeon ?: run {
        sender.sendFWDMessage("You're not editing any dungeons")
        return true
    }

    if (FinalDungeon.dungeons.values.any { it.name.equals(name.trim(), true) }) {
        sender.sendFWDMessage("Antoher dungeon with the same name already exists")
        return true
    }

    dungeon.name = name
    sender.sendFWDMessage("Dungeon name changed")

    return true
}