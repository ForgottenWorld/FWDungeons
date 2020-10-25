package it.forgottenworld.dungeons.command.edit.dungeon

import it.forgottenworld.dungeons.manager.DungeonEditManager
import it.forgottenworld.dungeons.model.Dungeon
import it.forgottenworld.dungeons.utils.sendFWDMessage
import org.bukkit.entity.Player

fun cmdDungeonDifficulty(sender: Player, args: Array<out String>): Boolean {
    if (args.isEmpty()) {
        sender.sendFWDMessage("Not enough arguments: please provide a difficulty")
        return true
    }

    Dungeon.Difficulty.values().map { it.toString() }.let {
        if (!it.contains(args[0].toLowerCase())) {
            sender.sendFWDMessage("Invalid argument, possible arguments: ${it.joinToString(", ")}")
            return true
        }

        val dungeon = DungeonEditManager.dungeonEditors[sender.uniqueId] ?: run {
            sender.sendFWDMessage("You're not editing any dungeons")
            return true
        }

        dungeon.difficulty = Dungeon.Difficulty.fromString(args[0].toLowerCase())!!
        sender.sendFWDMessage("Dungeon difficulty changed")

    }

    return true
}