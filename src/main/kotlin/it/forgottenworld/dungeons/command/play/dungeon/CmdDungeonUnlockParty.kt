package it.forgottenworld.dungeons.command.play.dungeon

import it.forgottenworld.dungeons.manager.DungeonManager.party
import it.forgottenworld.dungeons.utils.sendFWDMessage
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

fun cmdDungeonUnlockParty(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
    if (sender !is Player) return true

    val party = sender.party ?: run {
        sender.sendFWDMessage("You're currently not in a dungeon party")
        return true
    }

    when {
        !party.isLocked -> sender.sendFWDMessage("This dungeon party is already public")
        sender == party.leader -> {
            party.unlock()
            sender.sendFWDMessage("This dungeon party is now public, anyone can join")
        }
        else -> sender.sendFWDMessage("Only the dungeon party leader may make the party public")
    }

    return true
}