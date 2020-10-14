package it.forgottenworld.dungeons.command.edit.dungeon

import it.forgottenworld.dungeons.state.DungeonEditState
import it.forgottenworld.dungeons.state.DungeonState
import it.forgottenworld.dungeons.utils.sendFWDMessage
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

fun cmdDungeonEdit(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
    if (sender !is Player) return true

    if (args.isEmpty()) {
        sender.sendFWDMessage("Not enough arguments: please provide a dungeon id")
        return true
    }

    val id = args[0].toIntOrNull()
    if (id == null) {
        sender.sendFWDMessage("Dungeon id should be an integer")
        return true
    }

    val res = DungeonState.getDungeonById(id)?.let { d ->
        when {
            DungeonEditState.dungeonEditors.containsValue(d) -> false
            DungeonState.activeDungeons[id] == true -> false
            else -> {
                DungeonEditState.purgeWorkingData(sender)
                DungeonEditState.dungeonEditors[sender.uniqueId] = d
                true
            }
        }
    } == true

    sender.sendFWDMessage(
            if (res) "Now editing dungeon with id $id"
            else "No currently disabled dungeons found with id $id"
    )

    return true
}