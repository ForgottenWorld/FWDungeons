package it.forgottenworld.dungeons.core.command.play.dungeon

import com.google.inject.Inject
import it.forgottenworld.dungeons.api.command.SenderCommand
import it.forgottenworld.dungeons.core.config.Configuration
import it.forgottenworld.dungeons.core.config.Strings
import it.forgottenworld.dungeons.core.game.dungeon.DungeonManager
import it.forgottenworld.dungeons.core.utils.sendFWDMessage
import org.bukkit.command.CommandSender

class CmdDungeonReload @Inject constructor(
    private val configuration: Configuration,
    private val dungeonManager: DungeonManager
) : SenderCommand() {

    override fun command(sender: CommandSender, args: Array<out String>): Boolean {
        if (args.count() == 0 || args[0] != "confirm") {
            sender.sendFWDMessage(Strings.RELOAD_WARNING)
            return true
        }

        dungeonManager
            .getAllFinalDungeons()
            .flatMap { dungeonManager.getDungeonInstances(it).values }
            .forEach { it.evacuate() }

        dungeonManager.clearFinalDungeons()

        configuration.loadData()

        sender.sendFWDMessage(Strings.RELOADING_DUNGEONS_AND_INSTANCES)

        return true
    }
}