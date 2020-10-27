package it.forgottenworld.dungeons.command.play.dungeon

import it.forgottenworld.dungeons.config.ConfigManager
import it.forgottenworld.dungeons.service.DungeonService
import it.forgottenworld.dungeons.task.TriggerChecker
import it.forgottenworld.dungeons.utils.sendFWDMessage
import net.md_5.bungee.api.ChatColor
import org.bukkit.command.CommandSender

fun cmdDungeonReload(sender: CommandSender, args: Array<out String>): Boolean {
    if (args.count() == 0 || args[0] != "confirm") {
        sender.sendFWDMessage("${ChatColor.RED}WARNING: ${ChatColor.WHITE}Reloading will evacuate every instance. If you're sure you want to reload, use /fwd reload confirm")
        return true
    }

    DungeonService.dungeons.values.flatMap { it.instances.values }.forEach { it.evacuate() }
    DungeonService.dungeons.clear()
    DungeonService.playerInstances.clear()
    DungeonService.playersTriggering.clear()
    DungeonService.playerReturnPositions.clear()
    DungeonService.playerReturnGameModes.clear()

    ConfigManager.loadData()
    TriggerChecker.start()

    sender.sendFWDMessage("Reloading dungeons and instances...")

    return true
}