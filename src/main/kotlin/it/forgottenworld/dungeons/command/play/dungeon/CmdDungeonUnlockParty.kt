package it.forgottenworld.dungeons.command.play.dungeon

import it.forgottenworld.dungeons.manager.DungeonManager.dungeonInstance
import it.forgottenworld.dungeons.utils.ktx.sendFWDMessage
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

fun cmdDungeonUnlockParty(sender: CommandSender, args: Array<out String>): Boolean {
    if (sender !is Player) return true

    val instance = sender.dungeonInstance ?: run {
        sender.sendFWDMessage("You're currently not in a dungeon party")
        return true
    }

    when {
        !instance.isLocked -> sender.sendFWDMessage("This dungeon party is already public")
        sender == instance.leader -> {
            instance.unlock()
            sender.sendFWDMessage("This dungeon party is now public, anyone can join")
        }
        else -> sender.sendFWDMessage("Only the dungeon party leader may make the party public")
    }

    return true
}