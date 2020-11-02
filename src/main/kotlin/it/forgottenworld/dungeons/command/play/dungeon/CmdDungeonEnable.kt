package it.forgottenworld.dungeons.command.play.dungeon

import it.forgottenworld.dungeons.model.dungeon.FinalDungeon
import it.forgottenworld.dungeons.utils.ktx.sendFWDMessage
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

    val dungeon = FinalDungeon.dungeons[dungeonId] ?: run {
        sender.sendFWDMessage("No dungeon found with id $dungeonId")
        return true
    }

    if (dungeon.isBeingEdited) {
        sender.sendFWDMessage("Dungeon with id $dungeonId is being edited right now")
        return true
    }

    if (dungeon.isActive) {
        sender.sendFWDMessage("Dungeon with id $dungeonId is already active")
        return true
    }

    dungeon.isActive = true
    sender.sendFWDMessage("Dungeon (id: $dungeonId) was enabled")

    return true
}