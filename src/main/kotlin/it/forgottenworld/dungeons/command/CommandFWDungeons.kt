package it.forgottenworld.dungeons.command

import it.forgottenworld.dungeons.command.edit.dungeonCmdBindings
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player


class CommandFWDungeons : CommandExecutor {

    private val commandBindings = mapOf(
            "dungeon" to dungeonCmdBindings
    )

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean{

        if (sender is Player) {
            if (!sender.hasPermission("fwdungeons.command.${args[0]}.${args[1]}")) {
                sender.sendMessage("You don't have permission to use this command.")
                return true
            }

            if (commandBindings.containsKey(args[0]) &&
                    commandBindings[args[0]]?.containsKey(args[1]) == true) {
                return commandBindings[args[0]]?.get(args[1])?.invoke(sender, command, label, args)!!
            }

            return false
        }

        return false
    }

}