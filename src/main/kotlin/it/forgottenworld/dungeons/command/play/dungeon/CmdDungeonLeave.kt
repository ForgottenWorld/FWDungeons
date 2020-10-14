package it.forgottenworld.dungeons.command.play.dungeon

import it.forgottenworld.dungeons.state.DungeonState.party
import it.forgottenworld.dungeons.utils.sendFWDMessage
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

fun cmdDungeonLeave(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
    if (sender !is Player) return true

    sender.party?.let {
        if (it.inGame) {
            sender.sendFWDMessage("The instance has started, you can't leave now")
            return true
        }
        it.playerLeave(sender)
        sender.sendFWDMessage("You left the dungeon party")
    } ?: sender.sendFWDMessage("You're currently not in a dungeon party")

    return true
}