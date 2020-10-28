package it.forgottenworld.dungeons.command.edit.dungeon

import it.forgottenworld.dungeons.manager.DungeonBoxCreationManager
import org.bukkit.entity.Player

fun cmdDungeonPos1(sender: Player, args: Array<out String>): Boolean {
    DungeonBoxCreationManager.setDungeonBoxPos(sender, 1)
    return true
}