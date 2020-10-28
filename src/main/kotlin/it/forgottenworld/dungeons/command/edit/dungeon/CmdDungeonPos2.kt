package it.forgottenworld.dungeons.command.edit.dungeon

import it.forgottenworld.dungeons.manager.DungeonBoxCreationManager
import org.bukkit.entity.Player

fun cmdDungeonPos2(sender: Player, args: Array<out String>): Boolean {
    DungeonBoxCreationManager.setDungeonBoxPos(sender, 2)
    return true
}