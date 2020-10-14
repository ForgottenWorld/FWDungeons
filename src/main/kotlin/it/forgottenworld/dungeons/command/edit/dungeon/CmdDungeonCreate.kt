package it.forgottenworld.dungeons.command.edit.dungeon

import it.forgottenworld.dungeons.model.dungeon.Dungeon
import it.forgottenworld.dungeons.state.DungeonEditState
import it.forgottenworld.dungeons.state.DungeonState
import it.forgottenworld.dungeons.utils.sendFWDMessage
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

fun cmdDungeonCreate(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
    if (sender !is Player) return true

    val newId =
            DungeonEditState.wipDungeons.map{ it.id }.maxOrNull()?.plus(1)
                    ?: DungeonState.getMaxDungeonId() + 1

    Dungeon(newId).let {
        DungeonEditState.wipDungeons.add(it)
        DungeonEditState.dungeonEditors[sender.uniqueId] = it
    }

    sender.sendFWDMessage("Created dungeon with id $newId")
    return true
}