package it.forgottenworld.dungeons.api.command

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

abstract class PlayerCommand : TerminalCommand<Player> {

    override fun walkExecute(sender: CommandSender, args: Array<out String>): Boolean {
        if (sender is Player) {
            command(sender, args)
        } else {
            sender.sendMessage("This command may only be executed by players")
        }

        return true
    }
}