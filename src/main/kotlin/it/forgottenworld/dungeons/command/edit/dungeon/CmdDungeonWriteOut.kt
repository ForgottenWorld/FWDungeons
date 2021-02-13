package it.forgottenworld.dungeons.command.edit.dungeon

import it.forgottenworld.dungeons.command.api.CommandHandler
import it.forgottenworld.dungeons.config.ConfigManager
import it.forgottenworld.dungeons.config.Strings
import it.forgottenworld.dungeons.game.dungeon.EditableDungeon.Companion.editableDungeon
import it.forgottenworld.dungeons.utils.sendFWDMessage
import org.bukkit.entity.Player

class CmdDungeonWriteOut : CommandHandler<Player> {

    override fun command(sender: Player, args: Array<out String>): Boolean {
        val dungeon = sender.editableDungeon ?: run {
            sender.sendFWDMessage(Strings.NOT_EDITING_ANY_DUNGEONS)
            return true
        }

        val whatIsMissing = dungeon.whatIsMissingForWriteout()
        if (whatIsMissing.isNotEmpty()) {
            sender.sendFWDMessage(Strings.CANT_WRITEOUT_YET_MISSING.format(whatIsMissing))
            return true
        }

        val finalDungeon = dungeon.finalize()
        ConfigManager.saveDungeonConfig(finalDungeon, true)
        sender.sendFWDMessage(Strings.DUNGEON_EXPORTED)

        return true
    }
}