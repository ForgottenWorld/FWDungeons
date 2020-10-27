package it.forgottenworld.dungeons.command.edit.dungeon

import it.forgottenworld.dungeons.service.DungeonEditService
import it.forgottenworld.dungeons.model.dungeon.Difficulty
import it.forgottenworld.dungeons.utils.sendFWDMessage
import org.bukkit.entity.Player

fun cmdDungeonDifficulty(sender: Player, args: Array<out String>): Boolean {
    if (args.isEmpty()) {
        sender.sendFWDMessage("Not enough arguments: please provide a difficulty")
        return true
    }

    Difficulty.values().map { it.toString() }.let {
        if (!it.contains(args[0].toLowerCase())) {
            sender.sendFWDMessage("Invalid argument, possible arguments: ${it.joinToString(", ")}")
            return true
        }

        val dungeon = DungeonEditService.wipDungeons[sender.uniqueId] ?: run {
            sender.sendFWDMessage("You're not editing any dungeons")
            return true
        }

        dungeon.difficulty = Difficulty.fromString(args[0].toLowerCase())!!
        sender.sendFWDMessage("Dungeon difficulty changed")

    }

    return true
}