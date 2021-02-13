package it.forgottenworld.dungeons.command.play.dungeon

import it.forgottenworld.dungeons.command.api.CommandHandler
import it.forgottenworld.dungeons.config.ConfigManager
import it.forgottenworld.dungeons.config.Strings
import it.forgottenworld.dungeons.game.dungeon.FinalDungeon
import it.forgottenworld.dungeons.game.instance.DungeonFinalInstance
import it.forgottenworld.dungeons.game.interactiveregion.Trigger
import it.forgottenworld.dungeons.utils.sendFWDMessage
import org.bukkit.command.CommandSender

class CmdDungeonReload : CommandHandler<CommandSender> {

    override fun command(sender: CommandSender, args: Array<out String>): Boolean {
        if (args.count() == 0 || args[0] != "confirm") {
            sender.sendFWDMessage(Strings.RELOAD_WARNING)
            return true
        }

        FinalDungeon.dungeons.values.flatMap { it.instances.values }.forEach { it.evacuate() }
        FinalDungeon.dungeons.clear()
        DungeonFinalInstance.finalInstances.clear()
        Trigger.ActivationHandler.clearAllCollisions()


        ConfigManager.loadData()

        sender.sendFWDMessage(Strings.RELOADING_DUNGEONS_AND_INSTANCES)

        return true
    }
}