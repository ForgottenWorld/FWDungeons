package it.forgottenworld.dungeons.command.edit.dungeon

import it.forgottenworld.dungeons.command.edit.helpers.DungeonBoxCommandHelper
import org.bukkit.entity.Player

fun cmdDungeonPos2(sender: Player, args: Array<out String>): Boolean {
    DungeonBoxCommandHelper.setDungeonBoxPos(sender, 2)
    return true
}