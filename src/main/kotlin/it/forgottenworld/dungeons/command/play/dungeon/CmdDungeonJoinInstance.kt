package it.forgottenworld.dungeons.command.play.dungeon

import it.forgottenworld.dungeons.manager.DungeonManager.dungeonInstance
import it.forgottenworld.dungeons.manager.DungeonManager.dungeons
import it.forgottenworld.dungeons.utils.ktx.sendFWDMessage
import org.bukkit.entity.Player

fun cmdDungeonJoinInstance(sender: Player, args: Array<out String>): Boolean {
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

    val pass = if (args.count() > 2) args[2] else ""

    val dungeon = dungeons[dungeonId] ?: run {
        sender.sendFWDMessage("Invalid dungeon id")
        return true
    }

    if (!dungeon.active) {
        sender.sendFWDMessage("This dugeons is disabled")
        return true
    }

    if (sender.dungeonInstance != null) {
        sender.sendFWDMessage("You're already in a party")
        return true
    }

    val instance = dungeon.instances[instanceId] ?: run {
        sender.sendFWDMessage("Invalid instance id")
        return true
    }

    if (instance.isLocked && pass != instance.partyKey) {
        sender.sendFWDMessage("This dungeon party is private and you were not invited")
        return true
    }

    instance.onPlayerJoin(sender)

    return true
}