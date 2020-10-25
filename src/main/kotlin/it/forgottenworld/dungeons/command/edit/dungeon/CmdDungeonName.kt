package it.forgottenworld.dungeons.command.edit.dungeon

import it.forgottenworld.dungeons.manager.DungeonEditManager
import it.forgottenworld.dungeons.manager.DungeonManager
import it.forgottenworld.dungeons.utils.sendFWDMessage
import org.bukkit.entity.Player

fun cmdDungeonName(sender: Player, args: Array<out String>): Boolean {
    if (args.isEmpty()) {
        sender.sendFWDMessage("Not enough arguments: please provide a name")
        return true
    }

    val name = args.joinToString(" ")

    val dungeon = DungeonEditManager.dungeonEditors[sender.uniqueId] ?: run {
        sender.sendFWDMessage("You're not editing any dungeons")
        return true
    }

    DungeonManager.dungeons.values
            .find { it.name.equals(name.trim(), true) }
            ?.let {
                sender.sendFWDMessage("Antoher dungeon with the same name already exists")
                return true
            }
    DungeonEditManager.wipDungeons
            .find { it.name.equals(name.trim(), true) }
            ?.let {
                sender.sendFWDMessage("Antoher dungeon with the same name is being created by someone")
                return true
            }

    dungeon.name = name
    sender.sendFWDMessage("Dungeon name changed")

    return true
}