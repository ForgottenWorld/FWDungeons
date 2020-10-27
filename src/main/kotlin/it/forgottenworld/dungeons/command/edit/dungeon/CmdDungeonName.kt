package it.forgottenworld.dungeons.command.edit.dungeon

import it.forgottenworld.dungeons.service.DungeonEditService
import it.forgottenworld.dungeons.service.DungeonService
import it.forgottenworld.dungeons.utils.sendFWDMessage
import org.bukkit.entity.Player

fun cmdDungeonName(sender: Player, args: Array<out String>): Boolean {
    if (args.isEmpty()) {
        sender.sendFWDMessage("Not enough arguments: please provide a name")
        return true
    }

    val name = args.joinToString(" ")

    val dungeon = DungeonEditService.wipDungeons[sender.uniqueId] ?: run {
        sender.sendFWDMessage("You're not editing any dungeons")
        return true
    }

    if (DungeonService.dungeons.values.any { it.name.equals(name.trim(), true) }) {
        sender.sendFWDMessage("Antoher dungeon with the same name already exists")
        return true
    }

    if (DungeonEditService.wipDungeons.values.any { it.name.equals(name.trim(), true) }){
        sender.sendFWDMessage("Antoher dungeon with the same name is being created by someone")
        return true
    }

    dungeon.name = name
    sender.sendFWDMessage("Dungeon name changed")

    return true
}