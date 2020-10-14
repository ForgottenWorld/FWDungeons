package it.forgottenworld.dungeons.command

import it.forgottenworld.dungeons.command.play.dungeon.cmdDungeonBindings
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player


class CommandFWDungeons : CommandExecutor, TabExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean{
        if (args.isEmpty() || sender !is Player) return false

        if (!sender.hasPermission("fwdungeons.${args[0]}")) {
            sender.sendMessage("You don't have permission to use this command.")
            return true
        }

        return cmdDungeonBindings[args[0]]
                ?.let { it(sender, command, label, args.sliceArray(IntRange(1, args.lastIndex))) }
                ?: false
    }

    override fun onTabComplete(sender: CommandSender, cmd: Command, label: String, args: Array<String>) =
            if (!cmd.name.equals("fwdungeons", true) || sender !is Player) null
            else when (args.count()) {
                0 -> cmdDungeonBindings.keys.toList()
                1 -> cmdDungeonBindings.keys.filter { it.startsWith(args[0], true) }
                else -> null
            }
}