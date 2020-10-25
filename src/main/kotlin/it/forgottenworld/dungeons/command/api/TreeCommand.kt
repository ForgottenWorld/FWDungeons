package it.forgottenworld.dungeons.command.api

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

abstract class TreeCommand(val name: String, private val cmdTree: BranchingCommand) : CommandExecutor, TabCompleter {

    override fun onCommand(
            sender: CommandSender,
            command: Command,
            label: String,
            args: Array<out String>
    ) = when {
        args.isEmpty() -> false
        !sender.hasPermission("$name.${args[0]}") -> {
            sender.sendMessage("You don't have permission to use this command.")
            true
        }
        else -> cmdTree.walkExecute(sender, args)
    }

    private tailrec fun walkArgs(
            branch: BranchingCommand,
            subcommands: List<String>
    ): List<String>? {
        val key = subcommands.firstOrNull() ?: return null
        val cmd = branch.bindings[key] ?: return branch.bindings.keys.filter { it.startsWith(key) }
        return if (cmd is BranchingCommand) walkArgs(cmd, subcommands.drop(1)) else null
    }

    override fun onTabComplete(
            sender: CommandSender,
            cmd: Command,
            label: String,
            args: Array<out String>) =
            if (sender is Player && cmd.name.equals(name, true))
                walkArgs(cmdTree, args.toList())
            else null
}