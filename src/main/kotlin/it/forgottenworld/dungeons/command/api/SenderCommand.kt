package it.forgottenworld.dungeons.command.api

import org.bukkit.command.CommandSender

class SenderCommand(private val handler: (CommandSender, Array<out String>) -> Unit) : CommandNode {

    override fun walkExecute(sender: CommandSender, args: Array<out String>): Boolean {
        handler(sender, args)
        return true
    }
}