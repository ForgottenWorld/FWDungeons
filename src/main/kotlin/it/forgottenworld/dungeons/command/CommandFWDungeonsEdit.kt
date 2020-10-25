package it.forgottenworld.dungeons.command

import it.forgottenworld.dungeons.command.edit.activearea.activeAreaCmdBindings
import it.forgottenworld.dungeons.command.edit.dungeon.dungeonCmdBindings
import it.forgottenworld.dungeons.command.edit.trigger.triggerCmdBindings
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player


class CommandFWDungeonsEdit : CommandExecutor, TabExecutor {

    private val commandBindings = mapOf(
            "dungeon" to dungeonCmdBindings,
            "trigger" to triggerCmdBindings,
            "activearea" to activeAreaCmdBindings
    )

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (args.count() < 2 || sender !is Player) return false

        if (!sender.hasPermission("fwdungeonsedit.${args[0]}.${args[1]}")) {
            sender.sendMessage("You don't have permission to use this command.")
            return true
        }

        return if (commandBindings.containsKey(args[0]) && commandBindings[args[0]]?.containsKey(args[1]) == true)
            commandBindings[args[0]]?.get(args[1])
                ?.let { it(sender, command, label, args.sliceArray(IntRange(2, args.lastIndex))) }
                ?: false
        else false
    }

    override fun onTabComplete(sender: CommandSender, cmd: Command, label: String, args: Array<String>) =
            if (!cmd.name.equals("fwdungeonsedit", true) || sender !is Player) null
            else when (args.size) {
                0 -> commandBindings.keys.toList()
                1 -> commandBindings.keys.filter { it.startsWith(args[0], true) }
                2 -> commandBindings[args[0]]?.keys?.filter { it.startsWith(args[1], true) }
                else -> null
            }
}