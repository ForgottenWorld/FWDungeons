package it.forgottenworld.dungeons.command.play.dungeon

import it.forgottenworld.dungeons.config.ConfigManager
import it.forgottenworld.dungeons.event.listener.TriggerActivationHandler
import it.forgottenworld.dungeons.model.dungeon.FinalDungeon
import it.forgottenworld.dungeons.model.instance.DungeonFinalInstance
import it.forgottenworld.dungeons.utils.ktx.sendFWDMessage
import net.md_5.bungee.api.ChatColor
import org.bukkit.command.CommandSender

fun cmdDungeonReload(sender: CommandSender, args: Array<out String>): Boolean {
    if (args.count() == 0 || args[0] != "confirm") {
        sender.sendFWDMessage("${ChatColor.RED}WARNING: ${ChatColor.WHITE}Reloading will evacuate every instance. If you're sure you want to reload, use /fwd reload confirm")
        return true
    }

    FinalDungeon.dungeons.values.flatMap { it.instances.values }.forEach { it.evacuate() }
    FinalDungeon.dungeons.clear()
    DungeonFinalInstance.finalInstances.clear()
    TriggerActivationHandler.clearAllCollisions()


    ConfigManager.loadData()

    sender.sendFWDMessage("Reloading dungeons and instances...")

    return true
}