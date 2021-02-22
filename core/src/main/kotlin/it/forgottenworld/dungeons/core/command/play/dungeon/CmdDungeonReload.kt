package it.forgottenworld.dungeons.core.command.play.dungeon

import it.forgottenworld.dungeons.api.command.SenderCommand
import it.forgottenworld.dungeons.core.config.ConfigManager
import it.forgottenworld.dungeons.core.config.Strings
import it.forgottenworld.dungeons.core.game.dungeon.DungeonManager
import it.forgottenworld.dungeons.core.game.dungeon.DungeonManager.instances
import it.forgottenworld.dungeons.core.utils.sendFWDMessage
import org.bukkit.command.CommandSender

class CmdDungeonReload : SenderCommand() {

    override fun command(sender: CommandSender, args: Array<out String>): Boolean {
        if (args.count() == 0 || args[0] != "confirm") {
            sender.sendFWDMessage(Strings.RELOAD_WARNING)
            return true
        }

        DungeonManager.finalDungeons.values.flatMap { it.instances.values }.forEach { it.evacuate() }
        DungeonManager.finalDungeons.clear()

        ConfigManager.loadData()

        sender.sendFWDMessage(Strings.RELOADING_DUNGEONS_AND_INSTANCES)

        return true
    }
}