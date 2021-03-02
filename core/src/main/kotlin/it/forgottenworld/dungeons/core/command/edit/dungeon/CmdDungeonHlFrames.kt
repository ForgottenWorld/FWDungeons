package it.forgottenworld.dungeons.core.command.edit.dungeon

import com.google.inject.Inject
import it.forgottenworld.dungeons.api.command.PlayerCommand
import it.forgottenworld.dungeons.core.config.Strings
import it.forgottenworld.dungeons.core.game.DungeonManager
import it.forgottenworld.dungeons.core.utils.sendFWDMessage
import org.bukkit.entity.Player

class CmdDungeonHlFrames @Inject constructor(
    private val dungeonManager: DungeonManager
) : PlayerCommand() {

    override fun command(sender: Player, args: Array<out String>): Boolean {
        val dungeon = dungeonManager.getPlayerEditableDungeon(sender.uniqueId) ?: run {
            sender.sendFWDMessage(Strings.NOT_EDITING_ANY_DUNGEONS)
            return true
        }

        if (!dungeon.hasTestOrigin) {
            sender.sendFWDMessage(Strings.COULDNT_FIND_DUNGEON_TEST_INSTANCE)
            return true
        }

        dungeon.toggleEditorHighlights()

        sender.sendFWDMessage(Strings.TOGGLED_HIGHLIGHTED_FRAMES)

        return true
    }
}