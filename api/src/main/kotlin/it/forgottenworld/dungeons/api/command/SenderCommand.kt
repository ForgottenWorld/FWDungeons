package it.forgottenworld.dungeons.api.command

import org.bukkit.command.CommandSender

abstract class SenderCommand: TerminalCommand<CommandSender> {

    override fun walkExecute(sender: CommandSender, args: Array<out String>): Boolean {
        command(sender, args)
        return true
    }
}