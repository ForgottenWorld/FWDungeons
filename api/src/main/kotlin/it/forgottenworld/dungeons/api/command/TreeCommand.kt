package it.forgottenworld.dungeons.api.command

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

abstract class TreeCommand(
    private val name: String,
    private val trunk: BranchingCommand
) : CommandExecutor, TabCompleter {

    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): Boolean {
        if (args.isEmpty()) return false
        if (sender.hasPermission("$name.${args[0]}")) {
            return trunk.walkExecute(sender, args)
        }
        sender.sendMessage("You don't have permission to use this command.")
        return true
    }

    private tailrec fun walkArgs(
        branch: BranchingCommand,
        subcommands: List<String>
    ): List<String>? {
        val key = subcommands.firstOrNull() ?: return null
        val cmd = branch.bindings[key] ?: return branch.bindings.keys.filter { it.startsWith(key) }
        if (cmd !is BranchingCommand) return null
        return walkArgs(cmd, subcommands.drop(1))
    }

    override fun onTabComplete(
        sender: CommandSender,
        cmd: Command,
        label: String,
        args: Array<out String>
    ): List<String>? {
        if (sender !is Player) return null
        return walkArgs(trunk, args.toList())
    }
}