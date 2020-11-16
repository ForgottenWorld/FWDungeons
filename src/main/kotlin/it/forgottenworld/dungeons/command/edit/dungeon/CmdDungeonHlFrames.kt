package it.forgottenworld.dungeons.command.edit.dungeon

import it.forgottenworld.dungeons.config.Strings
import it.forgottenworld.dungeons.model.dungeon.EditableDungeon.Companion.editableDungeon
import it.forgottenworld.dungeons.utils.ktx.sendFWDMessage
import org.bukkit.entity.Player

fun cmdDungeonHlFrames(sender: Player, args: Array<out String>): Boolean {
    val dungeon = sender.editableDungeon ?: run {
        sender.sendFWDMessage(Strings.NOT_EDITING_ANY_DUNGEONS)
        return true
    }

    dungeon.testInstance?.toggleEditorHighlights() ?: run {
        sender.sendFWDMessage(Strings.COULDNT_FIND_DUNGEON_TEST_INSTANCE)
        return true
    }

    sender.sendFWDMessage(Strings.TOGGLED_HIGHLIGHTED_FRAMES)

    return true
}