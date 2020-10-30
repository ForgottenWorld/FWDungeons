package it.forgottenworld.dungeons.command.play.dungeon

import it.forgottenworld.dungeons.cli.getUnlockClickable
import it.forgottenworld.dungeons.manager.DungeonManager.dungeonInstance
import it.forgottenworld.dungeons.utils.ktx.sendFWDMessage
import org.bukkit.entity.Player

fun cmdDungeonLockParty(sender: Player, args: Array<out String>): Boolean {
    val instance = sender.dungeonInstance ?: run {
        sender.sendFWDMessage("You're currently not in a dungeon party")
        return true
    }

    when {
        instance.isLocked -> sender.sendFWDMessage("This dungeon party is already private")
        sender == instance.leader -> {
            instance.lock()
            sender.sendFWDMessage("The dungeon party is now private, invite players with /fwd invite. To make it public, click ${getUnlockClickable()}")
        }
        else -> sender.sendFWDMessage("Only the dungeon party leader may make the party private")
    }

    return true
}