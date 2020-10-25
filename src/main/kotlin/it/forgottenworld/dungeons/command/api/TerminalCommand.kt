package it.forgottenworld.dungeons.command.api

import org.bukkit.command.CommandSender

interface TerminalCommand: CommandNode {

    override fun walkExecute(sender: CommandSender, args: Array<out String>): Boolean
}