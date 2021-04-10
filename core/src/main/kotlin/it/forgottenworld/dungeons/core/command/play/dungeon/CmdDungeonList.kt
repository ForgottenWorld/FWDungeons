package it.forgottenworld.dungeons.core.command.play.dungeon

import com.google.inject.Inject
import it.forgottenworld.dungeons.api.command.PlayerCommand
import it.forgottenworld.dungeons.api.game.dungeon.DungeonManager
import org.bukkit.entity.Player

class CmdDungeonList @Inject constructor(
    private val dungeonManager: DungeonManager
) : PlayerCommand() {

    override fun command(sender: Player, args: Array<out String>): Boolean {
        val page = if (args.count() != 0 && args[0].toIntOrNull() != null) args[0].toInt() else 0
        dungeonManager.showDungeonListToPlayer(sender, page)
        return true
    }
}