package it.forgottenworld.dungeons.command.edit.dungeon

import it.forgottenworld.dungeons.manager.DungeonEditManager
import it.forgottenworld.dungeons.utils.sendFWDMessage
import it.forgottenworld.dungeons.utils.withRefSystemOrigin
import org.bukkit.entity.Player
import org.bukkit.util.BlockVector

fun cmdDungeonSetStart(sender: Player, args: Array<out String>): Boolean {
    val dungeon = DungeonEditManager.dungeonEditors[sender.uniqueId] ?: run {
        sender.sendFWDMessage("You're not editing any dungeons")
        return true
    }

    if (!dungeon.hasBox) {
        sender.sendFWDMessage("Dungeon box should be set before adding a starting location")
        return true
    }

    if (!DungeonEditManager.wipDungeons.contains(dungeon)) {
        sender.sendFWDMessage("This dungeon was already exported beforehand")
        return true
    }

    val wipOrigin = DungeonEditManager.wipDungeonOrigins[sender.uniqueId] ?: return true
    if (!dungeon.box.withOrigin(wipOrigin).containsPlayer(sender)) {
        sender.sendFWDMessage("You're outside of the dungeon box")
        return true
    }

    dungeon.startingLocation = sender.location
            .let { BlockVector(it.blockX, it.blockY, it.blockZ) }
            .withRefSystemOrigin(wipOrigin, BlockVector(0, 0, 0))

    sender.sendFWDMessage("Dungeon starting location set succesfully")

    return true
}