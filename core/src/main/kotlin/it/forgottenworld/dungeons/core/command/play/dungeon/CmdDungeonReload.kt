package it.forgottenworld.dungeons.core.command.play.dungeon

import com.google.inject.Inject
import it.forgottenworld.dungeons.api.command.SenderCommand
import it.forgottenworld.dungeons.core.FWDungeonsPlugin
import it.forgottenworld.dungeons.core.storage.Strings
import it.forgottenworld.dungeons.api.game.dungeon.DungeonManager
import it.forgottenworld.dungeons.core.utils.sendPrefixedMessage
import org.bukkit.command.CommandSender

class CmdDungeonReload @Inject constructor(
    private val plugin: FWDungeonsPlugin,
    private val dungeonManager: DungeonManager
) : SenderCommand() {

    override fun command(sender: CommandSender, args: Array<out String>): Boolean {
        if (args.count() == 0 || args[0] != "confirm") {
            sender.sendPrefixedMessage(Strings.RELOAD_WARNING)
            return true
        }

        dungeonManager
            .getAllFinalDungeons()
            .flatMap { dungeonManager.getDungeonInstances(it).values }
            .forEach { it.evacuate() }

        dungeonManager.clearFinalDungeons()

        plugin.loadData()

        sender.sendPrefixedMessage(Strings.RELOADING_DUNGEONS_AND_INSTANCES)

        return true
    }
}