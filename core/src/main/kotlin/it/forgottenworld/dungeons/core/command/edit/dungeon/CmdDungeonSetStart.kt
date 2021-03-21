package it.forgottenworld.dungeons.core.command.edit.dungeon

import com.google.inject.Inject
import it.forgottenworld.dungeons.api.command.PlayerCommand
import it.forgottenworld.dungeons.api.math.Vector3i
import it.forgottenworld.dungeons.core.storage.Strings
import it.forgottenworld.dungeons.api.game.dungeon.DungeonManager
import it.forgottenworld.dungeons.core.utils.sendPrefixedMessage
import org.bukkit.entity.Player

class CmdDungeonSetStart @Inject constructor(
    private val dungeonManager: DungeonManager
) : PlayerCommand() {

    override fun command(sender: Player, args: Array<out String>): Boolean {
        val dungeon = dungeonManager.getPlayerEditableDungeon(sender.uniqueId) ?: run {
            sender.sendPrefixedMessage(Strings.NOT_EDITING_ANY_DUNGEONS)
            return true
        }

        if (dungeon.box == null || !dungeon.hasTestOrigin) {
            sender.sendPrefixedMessage(Strings.DUNGEON_BOX_SHOULD_BE_SET_BEFORE_ADDING_STARTPOS)
            return true
        }

        if (!dungeon.box!!.containsPlayer(sender, dungeon.testOrigin)) {
            sender.sendPrefixedMessage(Strings.OUTSIDE_OF_DUNGEON_BOX)
            return true
        }

        dungeon.startingLocation = Vector3i
            .ofLocation(sender.location)
            .withRefSystemOrigin(dungeon.testOrigin, Vector3i.ZERO)

        sender.sendPrefixedMessage(Strings.DUNGEON_STARTPOS_SET)

        return true
    }
}