package it.forgottenworld.dungeons.command.play.dungeon

import it.forgottenworld.dungeons.config.ConfigManager
import it.forgottenworld.dungeons.state.DungeonState
import it.forgottenworld.dungeons.utils.sendFWDMessage
import net.md_5.bungee.api.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandSender

fun cmdDungeonReload(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
    if (args.count() == 0 || args[0] != "confirm") {
        sender.sendFWDMessage("${ChatColor.RED}WARNING: ${ChatColor.WHITE}Reloading will evacuate every instance. If you're sure you want to reload, use /fwd reload confirm")
        return true
    }

    DungeonState.dungeons.values.flatMap { it.instances }.forEach { DungeonState.evacuateDungeon(it.dungeon.id, it.id) }
    DungeonState.dungeons.clear()
    DungeonState.activeDungeons.clear()
    DungeonState.playerParties.clear()
    DungeonState.playersTriggering.clear()
    DungeonState.playerReturnPositions.clear()
    DungeonState.playerReturnGameModes.clear()

    ConfigManager.loadData()

    sender.sendFWDMessage("Reloading dungeons and instances...")

    return true
}