package it.forgottenworld.dungeons.api.command

import org.bukkit.command.CommandSender

interface CommandHandler {
    fun walkExecute(sender: CommandSender, args: Array<out String>): Boolean
}