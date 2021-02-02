package it.forgottenworld.dungeons.command.api

import org.bukkit.command.CommandSender

class BranchingCommand(val bindings: Map<String, CommandNode>) : CommandNode {

    override fun walkExecute(sender: CommandSender, args: Array<out String>) =
        args.isNotEmpty() && bindings[args[0]]?.walkExecute(sender, args.drop(1).toTypedArray()) ?: false
}