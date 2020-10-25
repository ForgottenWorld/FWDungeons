package it.forgottenworld.dungeons.command.play.dungeon

import it.forgottenworld.dungeons.manager.DungeonManager
import it.forgottenworld.dungeons.utils.sendFWDMessage
import org.bukkit.command.Command
import org.bukkit.command.CommandSender

fun cmdDungeonDisable(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
    if (args.count() < 1) {
        sender.sendFWDMessage("Please provide a dungeon id")
        return true
    }

    val dungeonId = args[0].toIntOrNull()

    if (dungeonId == null) {
        sender.sendFWDMessage("Dungeon id should be an integer")
        return true
    }

    val res = DungeonManager.dungeons[dungeonId]?.let {
        it.instances.forEach { inst -> DungeonManager.evacuateDungeon(it.id, inst.id) }
        DungeonManager.activeDungeons[dungeonId] = false
    } != null

    sender.sendFWDMessage(
            if (res)
                "Dungeon (id: $dungeonId) was disabled"
            else
                "No dungeon found with id $dungeonId")

    return true
}