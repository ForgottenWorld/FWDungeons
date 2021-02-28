package it.forgottenworld.dungeons.core.command.edit.dungeon

import it.forgottenworld.dungeons.api.command.PlayerCommand
import it.forgottenworld.dungeons.api.math.Vector3i
import it.forgottenworld.dungeons.core.config.Strings
import it.forgottenworld.dungeons.core.game.dungeon.DungeonManager.editableDungeon
import it.forgottenworld.dungeons.core.utils.sendFWDMessage
import org.bukkit.entity.Player

class CmdDungeonSetStart : PlayerCommand() {

    override fun command(sender: Player, args: Array<out String>): Boolean {
        val dungeon = sender.uniqueId.editableDungeon ?: run {
            sender.sendFWDMessage(Strings.NOT_EDITING_ANY_DUNGEONS)
            return true
        }

        if (dungeon.box == null || !dungeon.hasTestOrigin) {
            sender.sendFWDMessage(Strings.DUNGEON_BOX_SHOULD_BE_SET_BEFORE_ADDING_STARTPOS)
            return true
        }

        if (!dungeon.box!!.containsPlayer(sender, dungeon.testOrigin)) {
            sender.sendFWDMessage(Strings.OUTSIDE_OF_DUNGEON_BOX)
            return true
        }

        dungeon.startingLocation = Vector3i
            .ofLocation(sender.location)
            .withRefSystemOrigin(dungeon.testOrigin, Vector3i.ZERO)

        sender.sendFWDMessage(Strings.DUNGEON_STARTPOS_SET)

        return true
    }
}