package it.forgottenworld.dungeons.command.edit.dungeon

import it.forgottenworld.dungeons.model.dungeon.EditableDungeon.Companion.editableDungeon
import it.forgottenworld.dungeons.utils.ktx.sendFWDMessage
import it.forgottenworld.dungeons.utils.ktx.toBlockVector
import it.forgottenworld.dungeons.utils.ktx.withRefSystemOrigin
import org.bukkit.entity.Player
import org.bukkit.util.BlockVector

fun cmdDungeonSetStart(sender: Player, args: Array<out String>): Boolean {
    val dungeon = sender.editableDungeon ?: run {
        sender.sendFWDMessage("You're not editing any dungeons")
        return true
    }

    val testInstance = dungeon.testInstance
    if (dungeon.box == null || testInstance == null) {
        sender.sendFWDMessage("Dungeon box should be set before adding a starting position")
        return true
    }

    if (!testInstance.box.containsPlayer(sender)) {
        sender.sendFWDMessage("You're outside of the dungeon box")
        return true
    }

    dungeon.startingLocation = sender.location
            .toBlockVector()
            .withRefSystemOrigin(testInstance.origin, BlockVector(0, 0, 0))

    sender.sendFWDMessage("Dungeon starting location set succesfully")

    return true
}