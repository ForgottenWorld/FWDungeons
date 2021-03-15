package it.forgottenworld.dungeons.api.command

import org.bukkit.command.CommandSender

interface TerminalCommand<T: CommandSender> : CommandHandler {

    fun command(sender: T, args: Array<out String>): Boolean
}