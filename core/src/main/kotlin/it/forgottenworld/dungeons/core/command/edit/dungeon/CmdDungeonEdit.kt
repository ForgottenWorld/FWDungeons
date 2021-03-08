package it.forgottenworld.dungeons.core.command.edit.dungeon

import com.google.inject.Inject
import it.forgottenworld.dungeons.api.command.PlayerCommand
import it.forgottenworld.dungeons.core.config.Strings
import it.forgottenworld.dungeons.api.game.dungeon.DungeonManager
import it.forgottenworld.dungeons.core.utils.sendPrefixedMessage
import org.bukkit.entity.Player

class CmdDungeonEdit @Inject constructor(
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

        if (dungeonManager.getPlayerEditableDungeon(sender.uniqueId) != null) {
            sender.sendPrefixedMessage(Strings.ALREADY_EDITING_DUNGEON)
            return true
        }

        val dungeon = dungeonManager.getFinalDungeonById(id) ?: run {
            sender.sendPrefixedMessage(Strings.NO_DUNGEON_FOUND_WITH_ID.format(id))
            return true
        }

        if (dungeonManager.getDungeonInstances(dungeon).isEmpty()) {
            sender.sendPrefixedMessage(Strings.DUNGEON_WITH_ID_HAS_NO_INSTANCES_IMPORT_IT.format(id, id))
            return true
        }

        dungeon.putInEditMode(sender)

        return true
    }
}