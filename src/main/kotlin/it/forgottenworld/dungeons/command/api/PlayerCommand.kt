package it.forgottenworld.dungeons.command.api

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class PlayerCommand(private val handler: CommandHandler<Player>) : TerminalCommand {

    override fun walkExecute(sender: CommandSender, args: Array<out String>): Boolean {
        if (sender is Player) {
            handler.command(sender, args)
        } else {
            sender.sendMessage("This command may only be executed by players")
        }

        return true
    }
}