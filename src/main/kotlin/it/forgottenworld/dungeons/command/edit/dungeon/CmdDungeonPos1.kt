package it.forgottenworld.dungeons.command.edit.dungeon

import it.forgottenworld.dungeons.command.edit.helpers.DungeonBoxCommandHelper
import org.bukkit.entity.Player

fun cmdDungeonPos1(sender: Player, args: Array<out String>): Boolean {
    DungeonBoxCommandHelper.setDungeonBoxPos(sender, 1)
    return true
}