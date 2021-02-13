package it.forgottenworld.dungeons.command.api

import org.bukkit.command.CommandSender

class SenderCommand(private val handler: CommandHandler<CommandSender>) : CommandNode {

    override fun walkExecute(sender: CommandSender, args: Array<out String>): Boolean {
        handler.command(sender, args)
        return true
    }
}