package it.forgottenworld.dungeons.command.edit.dungeon

import it.forgottenworld.dungeons.manager.DungeonEditManager
import it.forgottenworld.dungeons.utils.sendFWDMessage
import org.bukkit.entity.Player

fun cmdDungeonDiscard(sender: Player, args: Array<out String>): Boolean {
    DungeonEditManager.wipDungeons[sender.uniqueId] ?: sender.sendFWDMessage("You're not editing any dungeons")

    DungeonEditManager.playerExitEditMode(sender)
    sender.sendFWDMessage("Dungeon discarded")

    return true
}