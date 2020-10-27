package it.forgottenworld.dungeons.command.edit.dungeon

import it.forgottenworld.dungeons.service.DungeonEditService
import it.forgottenworld.dungeons.utils.sendFWDMessage
import org.bukkit.entity.Player

fun cmdDungeonDiscard(sender: Player, args: Array<out String>): Boolean {
    DungeonEditService.wipDungeons[sender.uniqueId] ?: sender.sendFWDMessage("You're not editing any dungeons")

    DungeonEditService.playerExitEditMode(sender)
    sender.sendFWDMessage("Dungeon discarded")

    return true
}