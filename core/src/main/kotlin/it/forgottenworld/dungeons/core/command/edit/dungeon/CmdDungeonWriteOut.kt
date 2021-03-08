package it.forgottenworld.dungeons.core.command.edit.dungeon

import com.google.inject.Inject
import it.forgottenworld.dungeons.api.command.PlayerCommand
import it.forgottenworld.dungeons.api.game.dungeon.DungeonManager
import it.forgottenworld.dungeons.core.config.Strings
import it.forgottenworld.dungeons.core.utils.sendPrefixedMessage
import org.bukkit.entity.Player

class CmdDungeonWriteOut @Inject constructor(
    private val dungeonManager: DungeonManager
) : PlayerCommand() {

    override fun command(sender: Player, args: Array<out String>): Boolean {
        val dungeon = dungeonManager.getPlayerEditableDungeon(sender.uniqueId) ?: run {
            sender.sendPrefixedMessage(Strings.NOT_EDITING_ANY_DUNGEONS)
            return true
        }

        val whatIsMissing = dungeon.whatIsMissingForWriteout()
        if (whatIsMissing.isNotEmpty()) {
            sender.sendPrefixedMessage(Strings.CANT_WRITEOUT_YET_MISSING.format(whatIsMissing))
            return true
        }

        val finalDungeon = dungeon.finalize()
        dungeonManager.saveDungeonToStorage(finalDungeon)
        sender.sendPrefixedMessage(Strings.DUNGEON_EXPORTED)

        return true
    }
}