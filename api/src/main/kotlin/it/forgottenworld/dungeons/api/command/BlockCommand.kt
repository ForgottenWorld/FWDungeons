package it.forgottenworld.dungeons.api.command

import org.bukkit.command.BlockCommandSender
import org.bukkit.command.CommandSender

abstract class BlockCommand : TerminalCommand<BlockCommandSender> {

    override fun walkExecute(sender: CommandSender, args: Array<out String>): Boolean {
        if (sender is BlockCommandSender) {
            command(sender, args)
        } else {
            sender.sendMessage("This command may only be executed by command blocks")
        }
        return true
    }
}