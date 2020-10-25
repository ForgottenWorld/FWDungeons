package it.forgottenworld.dungeons.command.edit.dungeon

import it.forgottenworld.dungeons.manager.DungeonEditManager
import it.forgottenworld.dungeons.utils.sendFWDMessage
import org.bukkit.entity.Player

fun cmdDungeonDiscard(sender: Player, args: Array<out String>): Boolean {
    DungeonEditManager.dungeonEditors[sender.uniqueId] ?: sender.sendFWDMessage("You're not editing any dungeons")

    DungeonEditManager.purgeWorkingData(sender)
    sender.sendFWDMessage("Dungeon discarded")

    return true
}