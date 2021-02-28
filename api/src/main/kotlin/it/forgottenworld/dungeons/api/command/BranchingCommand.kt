package it.forgottenworld.dungeons.api.command

import org.bukkit.command.CommandSender

class BranchingCommand(val bindings: Map<String, CommandHandler>) : CommandHandler {

    override fun walkExecute(
        sender: CommandSender,
        args: Array<out String>
    ) = args.isNotEmpty() && bindings[args[0]]
        ?.walkExecute(sender, args.copyOfRange(1,args.size))
        ?: false
}