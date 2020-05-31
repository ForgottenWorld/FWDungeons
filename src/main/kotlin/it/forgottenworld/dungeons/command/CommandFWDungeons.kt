package it.forgottenworld.dungeons.command

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player


class CommandFWDungeons : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean{
        if (sender is Player) {
            if (!sender.hasPermission("fwdungeons.command")) {
                sender.sendMessage("You don't have permission to use this command.")
                return true
            }
        }

        return false
    }

}