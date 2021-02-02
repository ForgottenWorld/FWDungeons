package it.forgottenworld.dungeons.command.edit.dungeon

import it.forgottenworld.dungeons.config.Strings
import it.forgottenworld.dungeons.model.dungeon.EditableDungeon.Companion.editableDungeon
import it.forgottenworld.dungeons.utils.sendFWDMessage
import it.forgottenworld.dungeons.utils.toBlockVector
import it.forgottenworld.dungeons.utils.withRefSystemOrigin
import org.bukkit.entity.Player
import org.bukkit.util.BlockVector

fun cmdDungeonSetStart(sender: Player, args: Array<out String>): Boolean {
    val dungeon = sender.editableDungeon ?: run {
        sender.sendFWDMessage(Strings.NOT_EDITING_ANY_DUNGEONS)
        return true
    }

    val testInstance = dungeon.testInstance
    if (dungeon.box == null || testInstance == null) {
        sender.sendFWDMessage(Strings.DUNGEON_BOX_SHOULD_BE_SET_BEFORE_ADDING_STARTPOS)
        return true
    }

    if (!testInstance.box.containsPlayer(sender)) {
        sender.sendFWDMessage(Strings.OUTSIDE_OF_DUNGEON_BOX)
        return true
    }

    dungeon.startingLocation = sender.location
        .toBlockVector()
        .withRefSystemOrigin(testInstance.origin, BlockVector(0, 0, 0))

    sender.sendFWDMessage(Strings.DUNGEON_STARTPOS_SET)

    return true
}