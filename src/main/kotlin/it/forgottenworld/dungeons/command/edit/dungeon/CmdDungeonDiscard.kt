package it.forgottenworld.dungeons.command.edit.dungeon

import it.forgottenworld.dungeons.state.DungeonEditState
import it.forgottenworld.dungeons.utils.sendFWDMessage
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

fun cmdDungeonDiscard(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
    if (sender !is Player) return true

    DungeonEditState.dungeonEditors[sender.uniqueId] ?: sender.sendFWDMessage("You're not editing any dungeons")

    DungeonEditState.purgeWorkingData(sender)
    sender.sendFWDMessage("Dungeon discarded")

    return true
}