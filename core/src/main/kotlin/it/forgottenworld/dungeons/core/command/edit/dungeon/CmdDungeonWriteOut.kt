package it.forgottenworld.dungeons.core.command.edit.dungeon

import it.forgottenworld.dungeons.api.command.PlayerCommand
import it.forgottenworld.dungeons.core.config.ConfigManager
import it.forgottenworld.dungeons.core.config.Strings
import it.forgottenworld.dungeons.core.game.dungeon.EditableDungeon.Companion.editableDungeon
import it.forgottenworld.dungeons.core.utils.sendFWDMessage
import org.bukkit.entity.Player

class CmdDungeonWriteOut : PlayerCommand() {

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
        ConfigManager.saveDungeonConfig(finalDungeon)
        sender.sendFWDMessage(Strings.DUNGEON_EXPORTED)

        return true
    }
}