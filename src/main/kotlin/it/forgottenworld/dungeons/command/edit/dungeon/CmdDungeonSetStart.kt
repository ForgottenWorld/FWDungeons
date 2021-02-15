package it.forgottenworld.dungeons.command.edit.dungeon

import it.forgottenworld.dungeons.command.api.CommandHandler
import it.forgottenworld.dungeons.config.Strings
import it.forgottenworld.dungeons.game.dungeon.EditableDungeon.Companion.editableDungeon
import it.forgottenworld.dungeons.utils.Vector3i
import it.forgottenworld.dungeons.utils.sendFWDMessage
import it.forgottenworld.dungeons.utils.toVector3i
import it.forgottenworld.dungeons.utils.withRefSystemOrigin
import org.bukkit.entity.Player

class CmdDungeonSetStart : CommandHandler<Player> {

    override fun command(sender: Player, args: Array<out String>): Boolean {
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

        dungeon.startingLocation = sender
            .location
            .toVector3i()
            .withRefSystemOrigin(testInstance.origin, Vector3i(0, 0, 0))

        sender.sendFWDMessage(Strings.DUNGEON_STARTPOS_SET)

        return true
    }
}