package it.forgottenworld.dungeons.command

import it.forgottenworld.dungeons.command.play.cmdBindings
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player


class CommandFWDungeons : CommandExecutor, TabExecutor {

    private val commandBindings = cmdBindings

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean{
        if (args.count() < 1) return false

        if (sender is Player) {
            if (!sender.hasPermission("fwdungeons.${args[0]}")) {
                sender.sendMessage("You don't have permission to use this command.")
                return true
            }

            return commandBindings[args[0]]?.let {
                it(sender, command, label, args.sliceArray(IntRange(1, args.lastIndex)))
            } ?: false
        }

        return false
    }

    override fun onTabComplete(sender: CommandSender, cmd: Command, label: String, args: Array<String>): List<String>? {
        return if (cmd.name.equals("fwdungeons", true) && sender is Player) {
            when (args.count()) {
                0 -> commandBindings.keys.toList()
                1 -> commandBindings.keys.filter { it.startsWith(args[0], true) }
                else -> null
            }
        } else null
    }
}