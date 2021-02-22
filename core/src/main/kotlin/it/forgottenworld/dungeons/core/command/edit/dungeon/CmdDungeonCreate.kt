package it.forgottenworld.dungeons.core.command.edit.dungeon

import it.forgottenworld.dungeons.api.command.PlayerCommand
import it.forgottenworld.dungeons.core.config.Strings
import it.forgottenworld.dungeons.core.game.dungeon.EditableDungeon
import it.forgottenworld.dungeons.core.game.dungeon.DungeonManager.editableDungeon
import it.forgottenworld.dungeons.core.utils.sendFWDMessage
import org.bukkit.entity.Player

class CmdDungeonCreate : PlayerCommand() {

    override fun command(sender: Player, args: Array<out String>): Boolean {

        EditableDungeon(sender).let { sender.editableDungeon = it }

        sender.sendFWDMessage(Strings.CREATED_NEW_DUNGEON_NOW_IN_EDIT_MODE)
        return true
    }
}