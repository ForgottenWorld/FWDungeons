package it.forgottenworld.dungeons.command.edit.dungeon

import it.forgottenworld.dungeons.state.DungeonEditState
import it.forgottenworld.dungeons.utils.sendFWDMessage
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

fun cmdDungeonHlFrames(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
    if (sender !is Player) return true

    DungeonEditState.dungeonEditors[sender.uniqueId] ?: run {
        sender.sendFWDMessage("You're not editing any dungeons")
        return true
    }

    DungeonEditState.wipTestInstances[sender.uniqueId]?.toggleEditorHighlights() ?: run {
        sender.sendFWDMessage("Couldn't find dungeon test instance")
        return true
    }

    sender.sendFWDMessage("Toggled highlighted frames")

    return true
}