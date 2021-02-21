package it.forgottenworld.dungeons.core.command.edit.dungeon

import it.forgottenworld.dungeons.core.config.Strings
import it.forgottenworld.dungeons.core.game.dungeon.EditableDungeon.Companion.editableDungeon
import it.forgottenworld.dungeons.core.utils.sendFWDMessage
import it.forgottenworld.dungeons.api.command.PlayerCommand
import org.bukkit.entity.Player

class CmdDungeonHlFrames : PlayerCommand() {

    override fun command(sender: Player, args: Array<out String>): Boolean {
        val dungeon = sender.editableDungeon ?: run {
            sender.sendFWDMessage(Strings.NOT_EDITING_ANY_DUNGEONS)
            return true
        }

        if (!dungeon.hasTestBox) {
            sender.sendFWDMessage(Strings.COULDNT_FIND_DUNGEON_TEST_INSTANCE)
            return true
        }

        dungeon.toggleEditorHighlights()

        sender.sendFWDMessage(Strings.TOGGLED_HIGHLIGHTED_FRAMES)

        return true
    }
}