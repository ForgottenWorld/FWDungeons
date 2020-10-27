package it.forgottenworld.dungeons.command.edit.dungeon

import it.forgottenworld.dungeons.model.dungeon.EditableDungeon
import it.forgottenworld.dungeons.service.DungeonEditService
import it.forgottenworld.dungeons.utils.sendFWDMessage
import org.bukkit.entity.Player

fun cmdDungeonCreate(sender: Player, args: Array<out String>): Boolean {

    EditableDungeon(-1000).let { DungeonEditService.wipDungeons[sender.uniqueId] = it }

    sender.sendFWDMessage("Created new dungeon. You're now in edit mode.")
    return true
}