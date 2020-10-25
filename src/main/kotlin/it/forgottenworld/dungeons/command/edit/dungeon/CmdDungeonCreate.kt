package it.forgottenworld.dungeons.command.edit.dungeon

import it.forgottenworld.dungeons.model.Dungeon
import it.forgottenworld.dungeons.manager.DungeonEditManager
import it.forgottenworld.dungeons.manager.DungeonManager
import it.forgottenworld.dungeons.utils.sendFWDMessage
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

fun cmdDungeonCreate(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
    if (sender !is Player) return true

    val newId =
            DungeonEditManager.wipDungeons.map{ it.id }.maxOrNull()?.plus(1)
                    ?: DungeonManager.getMaxDungeonId() + 1

    Dungeon(newId).let {
        DungeonEditManager.wipDungeons.add(it)
        DungeonEditManager.dungeonEditors[sender.uniqueId] = it
    }

    sender.sendFWDMessage("Created dungeon with id $newId")
    return true
}