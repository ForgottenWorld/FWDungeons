package it.forgottenworld.dungeons.command.play.dungeon

import it.forgottenworld.dungeons.manager.DungeonManager.dungeonInstance
import it.forgottenworld.dungeons.utils.ktx.sendFWDMessage
import org.bukkit.entity.Player

fun cmdDungeonLeave(sender: Player, args: Array<out String>): Boolean {
    sender.dungeonInstance?.let {
        if (it.inGame) {
            sender.sendFWDMessage("The instance has started, you can't leave now")
            return true
        }
        it.onPlayerLeave(sender)
    } ?: sender.sendFWDMessage("You're currently not in a dungeon party")

    return true
}