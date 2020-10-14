package it.forgottenworld.dungeons.command.edit.dungeon

import it.forgottenworld.dungeons.model.dungeon.Dungeon
import it.forgottenworld.dungeons.state.DungeonEditState
import it.forgottenworld.dungeons.utils.sendFWDMessage
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

fun cmdDungeonDifficulty(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
    if (sender !is Player) return true
    if (args.isEmpty()) {
        sender.sendFWDMessage("Not enough arguments: please provide a difficulty")
        return true
    }

    Dungeon.Difficulty.values().map { it.toString() }.let {
        if (!it.contains(args[0].toLowerCase())) {
            sender.sendFWDMessage("Invalid argument, possible arguments: ${it.joinToString(", ")}")
            return true
        }

        val dungeon = DungeonEditState.dungeonEditors[sender.uniqueId] ?: run {
            sender.sendFWDMessage("You're not editing any dungeons")
            return true
        }

        dungeon.difficulty = Dungeon.Difficulty.fromString(args[0].toLowerCase())!!
        sender.sendFWDMessage("Dungeon difficulty changed")

    }

    return true
}