package it.forgottenworld.dungeons.command.play.dungeon

import it.forgottenworld.dungeons.manager.DungeonManager
import it.forgottenworld.dungeons.utils.sendFWDMessage
import org.bukkit.command.Command
import org.bukkit.command.CommandSender

fun cmdDungeonEvacuate(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
    if (args.count() < 2) {
        sender.sendFWDMessage("Please provide both a dungeon and instance id")
        return true
    }

    val dungeonId = args[0].toIntOrNull()
    val instanceId = args[1].toIntOrNull()

    if (dungeonId == null || instanceId == null) {
        sender.sendFWDMessage("Dungeon id and instance id should both be integers")
        return true
    }

    sender.sendFWDMessage(
            if (DungeonManager.evacuateDungeon(dungeonId, instanceId))
                "All adventurers were brought back to safety and the instance was reset"
            else
                "Dungeon instance not found")

    return true
}