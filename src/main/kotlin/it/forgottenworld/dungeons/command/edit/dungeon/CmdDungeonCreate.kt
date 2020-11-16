package it.forgottenworld.dungeons.command.edit.dungeon

import it.forgottenworld.dungeons.config.Strings
import it.forgottenworld.dungeons.model.dungeon.EditableDungeon
import it.forgottenworld.dungeons.model.dungeon.EditableDungeon.Companion.editableDungeon
import it.forgottenworld.dungeons.utils.ktx.sendFWDMessage
import org.bukkit.entity.Player

fun cmdDungeonCreate(sender: Player, args: Array<out String>): Boolean {

    EditableDungeon(sender).let { sender.editableDungeon = it }

    sender.sendFWDMessage(Strings.CREATED_NEW_DUNGEON_NOW_IN_EDIT_MODE)
    return true
}