package it.forgottenworld.dungeons.command.play.dungeon

import it.forgottenworld.dungeons.config.ConfigManager
import it.forgottenworld.dungeons.manager.DungeonManager
import it.forgottenworld.dungeons.utils.sendFWDMessage
import net.md_5.bungee.api.ChatColor
import org.bukkit.command.CommandSender

fun cmdDungeonReload(sender: CommandSender, args: Array<out String>): Boolean {
    if (args.count() == 0 || args[0] != "confirm") {
        sender.sendFWDMessage("${ChatColor.RED}WARNING: ${ChatColor.WHITE}Reloading will evacuate every instance. If you're sure you want to reload, use /fwd reload confirm")
        return true
    }

    DungeonManager.dungeons.values.flatMap { it.instances.values }.forEach { it.evacuate() }
    DungeonManager.dungeons.clear()
    DungeonManager.playerInstances.clear()
    DungeonManager.playersTriggering.clear()
    DungeonManager.playerReturnPositions.clear()
    DungeonManager.playerReturnGameModes.clear()

    ConfigManager.loadData()

    sender.sendFWDMessage("Reloading dungeons and instances...")

    return true
}