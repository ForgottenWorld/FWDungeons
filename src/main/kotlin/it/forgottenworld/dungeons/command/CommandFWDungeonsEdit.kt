package it.forgottenworld.dungeons.command

import it.forgottenworld.dungeons.command.edit.activeAreaCmdBindings
import it.forgottenworld.dungeons.command.edit.dungeonCmdBindings
import it.forgottenworld.dungeons.command.edit.triggerCmdBindings
import org.bukkit.command.*
import org.bukkit.entity.Player


class CommandFWDungeonsEdit : CommandExecutor, TabExecutor {

    private val commandBindings = mapOf(
            "dungeon" to dungeonCmdBindings,
            "trigger" to triggerCmdBindings,
            "activearea" to activeAreaCmdBindings
    )

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (args.count() < 2) return false

        if (sender is Player) {
            if (!sender.hasPermission("fwdungeonsedit.${args[0]}.${args[1]}")) {
                sender.sendMessage("You don't have permission to use this command.")
                return true
            }

            if (commandBindings.containsKey(args[0]) &&
                    commandBindings[args[0]]?.containsKey(args[1]) == true) {
                return commandBindings[args[0]]?.get(args[1])?.let {
                    it(sender, command, label, args.sliceArray(IntRange(2, args.lastIndex)))
                } ?: false
            }

            return false
        }

        return false
    }

    override fun onTabComplete(sender: CommandSender, cmd: Command, label: String, args: Array<String>): List<String>? {
        return if (cmd.name.equals("fwdungeonsedit", true) && sender is Player) {
             when (args.count()) {
                0 -> commandBindings.keys.toList()
                1 -> commandBindings.keys.filter { it.startsWith(args[0], true) }
                2 -> commandBindings[args[0]]?.keys?.filter { it.startsWith(args[1], true) }
                else -> null
             }
        } else null
    }
}