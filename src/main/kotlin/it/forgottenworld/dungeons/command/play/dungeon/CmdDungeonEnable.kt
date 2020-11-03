package it.forgottenworld.dungeons.command.play.dungeon

import it.forgottenworld.dungeons.model.dungeon.FinalDungeon
import it.forgottenworld.dungeons.utils.ktx.sendFWDMessage
import org.bukkit.command.CommandSender

fun cmdDungeonEnable(sender: CommandSender, args: Array<out String>): Boolean {
    if (args.count() < 1) {
        sender.sendFWDMessage("Please provide a dungeon id")
        return true
    }

    val id = args[0].toIntOrNull() ?: run {
        sender.sendFWDMessage("Dungeon id should be an integer")
        return true
    }

    val dungeon = FinalDungeon.dungeons[id] ?: run {
        sender.sendFWDMessage("No dungeon found with id $id")
        return true
    }

    if (dungeon.isBeingEdited) {
        sender.sendFWDMessage("Dungeon with id $id is being edited right now")
        return true
    }

    if (dungeon.isActive) {
        sender.sendFWDMessage("Dungeon with id $id is already active")
        return true
    }

    if (dungeon.instances.isEmpty()) {
        sender.sendFWDMessage("Dungeon with id $id has no instances, import it with /fwde d import $id first")
        return true
    }

    dungeon.isActive = true
    sender.sendFWDMessage("Dungeon with id $id was enabled")

    return true
}