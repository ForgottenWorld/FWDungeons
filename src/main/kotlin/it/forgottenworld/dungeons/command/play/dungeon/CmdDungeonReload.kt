package it.forgottenworld.dungeons.command.play.dungeon

import it.forgottenworld.dungeons.config.ConfigManager
import it.forgottenworld.dungeons.config.Strings
import it.forgottenworld.dungeons.event.listener.TriggerActivationHandler
import it.forgottenworld.dungeons.model.dungeon.FinalDungeon
import it.forgottenworld.dungeons.model.instance.DungeonFinalInstance
import it.forgottenworld.dungeons.utils.ktx.sendFWDMessage
import org.bukkit.command.CommandSender

fun cmdDungeonReload(sender: CommandSender, args: Array<out String>): Boolean {
    if (args.count() == 0 || args[0] != "confirm") {
        sender.sendFWDMessage(Strings.RELOAD_WARNING)
        return true
    }

    FinalDungeon.dungeons.values.flatMap { it.instances.values }.forEach { it.evacuate() }
    FinalDungeon.dungeons.clear()
    DungeonFinalInstance.finalInstances.clear()
    TriggerActivationHandler.clearAllCollisions()


    ConfigManager.loadData()

    sender.sendFWDMessage(Strings.RELOADING_DUNGEONS_AND_INSTANCES)

    return true
}