package it.forgottenworld.dungeons.command.edit.dungeon

import it.forgottenworld.dungeons.model.dungeon.EditableDungeon.Companion.editableDungeon
import it.forgottenworld.dungeons.utils.ktx.sendFWDMessage
import org.bukkit.entity.Player

fun cmdDungeonHlFrames(sender: Player, args: Array<out String>): Boolean {
    val dungeon = sender.editableDungeon ?: run {
        sender.sendFWDMessage("You're not editing any dungeons")
        return true
    }

    dungeon.testInstance?.toggleEditorHighlights() ?: run {
        sender.sendFWDMessage("Couldn't find dungeon test instance")
        return true
    }

    sender.sendFWDMessage("Toggled highlighted frames")

    return true
}