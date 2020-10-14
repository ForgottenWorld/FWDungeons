package it.forgottenworld.dungeons.command.play.dungeon

import it.forgottenworld.dungeons.state.DungeonState
import it.forgottenworld.dungeons.utils.sendFWDMessage
import org.bukkit.command.Command
import org.bukkit.command.CommandSender

fun cmdDungeonEnable(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
    if (args.count() < 1) {
        sender.sendFWDMessage("Please provide a dungeon id")
        return true
    }

    val dungeonId = args[0].toIntOrNull() ?: run {
        sender.sendFWDMessage("Dungeon id should be an integer")
        return true
    }

    val res = if (DungeonState.dungeons.contains(dungeonId)) {
        DungeonState.activeDungeons[dungeonId] = true
        true
    } else false

    sender.sendFWDMessage(
            if (res) "Dungeon (id: $dungeonId) was enabled"
            else "No dungeon found with id $dungeonId"
    )

    return true
}