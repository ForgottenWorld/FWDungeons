package it.forgottenworld.dungeons.command.api

import org.bukkit.command.CommandSender

fun interface CommandHandler<T : CommandSender> {
    fun command(sender: T, args: Array<out String>): Boolean
}