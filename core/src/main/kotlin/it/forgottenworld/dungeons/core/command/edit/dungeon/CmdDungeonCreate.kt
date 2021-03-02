package it.forgottenworld.dungeons.core.command.edit.dungeon

import com.google.inject.Inject
import it.forgottenworld.dungeons.api.command.PlayerCommand
import it.forgottenworld.dungeons.core.config.Strings
import it.forgottenworld.dungeons.core.game.DungeonManager
import it.forgottenworld.dungeons.core.game.dungeon.DungeonFactory
import it.forgottenworld.dungeons.core.utils.sendFWDMessage
import org.bukkit.entity.Player

class CmdDungeonCreate @Inject constructor(
    private val dungeonFactory: DungeonFactory,
    private val dungeonManager: DungeonManager
) : PlayerCommand() {

    override fun command(sender: Player, args: Array<out String>): Boolean {
        dungeonManager.setPlayerEditableDungeon(
            sender.uniqueId,
            dungeonFactory.createEditable(sender)
        )

        sender.sendFWDMessage(Strings.CREATED_NEW_DUNGEON_NOW_IN_EDIT_MODE)
        return true
    }
}