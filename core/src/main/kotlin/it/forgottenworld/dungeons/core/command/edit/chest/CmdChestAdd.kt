package it.forgottenworld.dungeons.core.command.edit.chest

import com.google.inject.Inject
import it.forgottenworld.dungeons.api.command.PlayerCommand
import it.forgottenworld.dungeons.api.game.dungeon.DungeonManager
import it.forgottenworld.dungeons.api.math.Vector3i
import it.forgottenworld.dungeons.core.game.dungeon.subelement.chest.ChestImpl
import it.forgottenworld.dungeons.core.storage.Strings
import it.forgottenworld.dungeons.core.utils.firstGap
import it.forgottenworld.dungeons.core.utils.getTargetSolidBlock
import it.forgottenworld.dungeons.core.utils.sendPrefixedMessage
import org.bukkit.entity.Player

class CmdChestAdd @Inject constructor(
    private val dungeonManager: DungeonManager
) : PlayerCommand() {

    override fun command(sender: Player, args: Array<out String>): Boolean {
        val block = sender.getTargetSolidBlock() ?: run {
            sender.sendPrefixedMessage(Strings.YOU_NEED_TO_BE_TARGETING)
            return true
        }

        val dungeon = dungeonManager.getPlayerEditableDungeon(sender.uniqueId) ?: run {
            sender.sendPrefixedMessage(Strings.NOT_EDITING_ANY_DUNGEONS)
            return true
        }

        if (!dungeon.hasTestOrigin) return true

        val id = dungeon.chests.keys.firstGap()
        val loc = Vector3i.ofLocation(block.location).withRefSystemOrigin(Vector3i.ZERO, dungeon.testOrigin)
        dungeon.chests[id] = ChestImpl(id, loc)
        sender.sendPrefixedMessage(Strings.CHEST_ADDED_SUCCESFULLY)
        return true
    }
}