package it.forgottenworld.dungeons.command.edit.dungeon

import it.forgottenworld.dungeons.model.dungeon.EditableDungeon
import it.forgottenworld.dungeons.model.dungeon.EditableDungeon.Companion.editableDungeon
import it.forgottenworld.dungeons.utils.ktx.sendFWDMessage
import org.bukkit.entity.Player

fun cmdDungeonCreate(sender: Player, args: Array<out String>): Boolean {

    EditableDungeon(sender).let { sender.editableDungeon = it }

    sender.sendFWDMessage("Created new dungeon. You're now in edit mode.")
    return true
}