package it.forgottenworld.dungeons.command.edit.dungeon

import it.forgottenworld.dungeons.service.DungeonEditService
import it.forgottenworld.dungeons.utils.sendFWDMessage
import org.bukkit.entity.Player

fun cmdDungeonHlFrames(sender: Player, args: Array<out String>): Boolean {
    val dungeon = DungeonEditService.wipDungeons[sender.uniqueId] ?: run {
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