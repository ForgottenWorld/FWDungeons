package it.forgottenworld.dungeons.core.command.edit.dungeon

import com.google.inject.Inject
import it.forgottenworld.dungeons.api.command.PlayerCommand
import it.forgottenworld.dungeons.api.game.dungeon.DungeonManager
import it.forgottenworld.dungeons.api.math.Vector3i
import it.forgottenworld.dungeons.core.storage.Strings
import it.forgottenworld.dungeons.core.utils.getTargetSolidBlock
import it.forgottenworld.dungeons.core.utils.sendPrefixedMessage
import org.bukkit.entity.Player

class CmdDungeonImport @Inject constructor(
    private val dungeonManager: DungeonManager
) : PlayerCommand() {

    override fun command(sender: Player, args: Array<out String>): Boolean {
        if (args.isEmpty()) {
            sender.sendPrefixedMessage(Strings.NEA_PROVIDE_DUNGEON_ID)
            return true
        }

        val id = args[0].toIntOrNull()
        if (id == null) {
            sender.sendPrefixedMessage(Strings.DUNGEON_ID_SHOULD_BE_INT)
            return true
        }

        val block = sender.getTargetSolidBlock() ?: run {
            sender.sendPrefixedMessage(Strings.YOU_NEED_TO_BE_TARGETING)
            return true
        }

        val dungeon = dungeonManager.getFinalDungeonById(id) ?: run {
            sender.sendPrefixedMessage(Strings.NO_DUNGEON_FOUND_WITH_ID, id)
            return true
        }

        if (!dungeon.import(Vector3i.ofBlock(block))) {
            sender.sendPrefixedMessage(Strings.THIS_DUNGEON_ALREADY_HAS_INSTANCES)
            return true
        }

        sender.sendPrefixedMessage(Strings.DUNGEON_IMPORTED)
        return true
    }
}