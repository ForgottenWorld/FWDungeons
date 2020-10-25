package it.forgottenworld.dungeons.command.play.dungeon

import it.forgottenworld.dungeons.cli.getUnlockClickable
import it.forgottenworld.dungeons.manager.DungeonManager.party
import it.forgottenworld.dungeons.utils.sendFWDMessage
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

fun cmdDungeonLockParty(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
    if (sender !is Player) return true

    val party = sender.party ?: run {
        sender.sendFWDMessage("You're currently not in a dungeon party")
        return true
    }

    when {
        party.isLocked -> sender.sendFWDMessage("This dungeon party is already private")
        sender == party.leader -> {
            party.lock()
            sender.sendFWDMessage("The dungeon party is now private, invite players with /fwd invite. To make it public, click ${getUnlockClickable()}")
        }
        else -> sender.sendFWDMessage("Only the dungeon party leader may make the party private")
    }

    return true
}