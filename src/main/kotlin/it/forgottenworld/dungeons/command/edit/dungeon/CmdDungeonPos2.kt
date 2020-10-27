package it.forgottenworld.dungeons.command.edit.dungeon

import it.forgottenworld.dungeons.service.DungeonBoxCreationService
import org.bukkit.entity.Player

fun cmdDungeonPos2(sender: Player, args: Array<out String>): Boolean {
    DungeonBoxCreationService.setDungeonBoxPos(sender, 2)
    return true
}