package it.forgottenworld.dungeons.core.command.play.dungeon

import it.forgottenworld.dungeons.api.command.SenderCommand
import it.forgottenworld.dungeons.core.config.ConfigManager
import it.forgottenworld.dungeons.core.config.Strings
import it.forgottenworld.dungeons.core.game.dungeon.FinalDungeon
import it.forgottenworld.dungeons.core.game.instance.DungeonInstanceImpl
import it.forgottenworld.dungeons.core.utils.sendFWDMessage
import org.bukkit.command.CommandSender

class CmdDungeonReload : SenderCommand() {

    override fun command(sender: CommandSender, args: Array<out String>): Boolean {
        if (args.count() == 0 || args[0] != "confirm") {
            sender.sendFWDMessage(Strings.RELOAD_WARNING)
            return true
        }

        FinalDungeon.dungeons.values.flatMap { it.instances.values }.forEach { it.evacuate() }
        FinalDungeon.dungeons.clear()
        DungeonInstanceImpl.finalInstances.clear()

        ConfigManager.loadData()

        sender.sendFWDMessage(Strings.RELOADING_DUNGEONS_AND_INSTANCES)

        return true
    }
}