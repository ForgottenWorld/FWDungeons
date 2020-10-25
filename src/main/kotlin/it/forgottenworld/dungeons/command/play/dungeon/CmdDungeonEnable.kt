package it.forgottenworld.dungeons.command.play.dungeon

import it.forgottenworld.dungeons.manager.DungeonManager
import it.forgottenworld.dungeons.utils.sendFWDMessage
import org.bukkit.command.CommandSender

fun cmdDungeonEnable(sender: CommandSender, args: Array<out String>): Boolean {
    if (args.count() < 1) {
        sender.sendFWDMessage("Please provide a dungeon id")
        return true
    }

    val dungeonId = args[0].toIntOrNull() ?: run {
        sender.sendFWDMessage("Dungeon id should be an integer")
        return true
    }

    val res = if (DungeonManager.dungeons.contains(dungeonId)) {
        DungeonManager.activeDungeons[dungeonId] = true
        true
    } else false

    sender.sendFWDMessage(
            if (res) "Dungeon (id: $dungeonId) was enabled"
            else "No dungeon found with id $dungeonId"
    )

    return true
}