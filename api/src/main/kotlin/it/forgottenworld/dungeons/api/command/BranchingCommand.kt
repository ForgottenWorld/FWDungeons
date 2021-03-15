package it.forgottenworld.dungeons.api.command

import org.bukkit.command.CommandSender

class BranchingCommand(val bindings: Map<String, CommandHandler>) : CommandHandler {

    override fun walkExecute(
        sender: CommandSender,
        args: Array<out String>
    ): Boolean {
        if (args.isEmpty()) return false
        val binding = bindings[args[0]] ?: return false
        return binding.walkExecute(sender, args.copyOfRange(1, args.size))
    }
}