package it.forgottenworld.dungeons.command.api

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class PlayerCommand(private val handler: (Player, Array<out String>) -> Unit): TerminalCommand {

    override fun walkExecute(sender: CommandSender, args: Array<out String>): Boolean {
        if (sender is Player)
            handler(sender, args)
        else
            sender.sendMessage("This command may only be executed by players")

        return true
    }
}