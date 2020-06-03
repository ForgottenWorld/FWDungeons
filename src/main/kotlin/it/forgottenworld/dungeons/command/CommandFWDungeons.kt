package it.forgottenworld.dungeons.command

import it.forgottenworld.dungeons.command.edit.dungeonCmdBindings
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player


class CommandFWDungeons : CommandExecutor, TabExecutor {

    private val commandBindings = mapOf(
            "dungeon" to dungeonCmdBindings
    )

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean{

        if (sender is Player) {
            if (!sender.hasPermission("fwdungeons.${args[0]}.${args[1]}")) {
                sender.sendMessage("You don't have permission to use this command.")
                return true
            }

            return commandBindings[args[0]]?.get(args[1])?.let {
                it(sender, command, label, args)
            } ?: false
        }

        return false
    }

    override fun onTabComplete(sender: CommandSender, cmd: Command, label: String, args: Array<String>): List<String>? {
        return if (cmd.name.equals("fwdungeons", true) && sender is Player) {
            when (args.count()) {
                0 -> commandBindings.keys.toList()
                1 -> commandBindings.keys.filter { it.startsWith(args[0], true) }
                2 -> commandBindings[args[0]]?.keys?.filter { it.startsWith(args[1], true) }
                else -> null
            }
        } else null
    }
}