package it.forgottenworld.dungeons.command.edit.dungeon

import it.forgottenworld.dungeons.command.api.CommandHandler
import it.forgottenworld.dungeons.config.Strings
import it.forgottenworld.dungeons.game.dungeon.EditableDungeon
import it.forgottenworld.dungeons.game.dungeon.EditableDungeon.Companion.editableDungeon
import it.forgottenworld.dungeons.utils.sendFWDMessage
import org.bukkit.entity.Player

class CmdDungeonCreate : CommandHandler<Player> {

    override fun command(sender: Player, args: Array<out String>): Boolean {

        EditableDungeon(sender).let { sender.editableDungeon = it }

        sender.sendFWDMessage(Strings.CREATED_NEW_DUNGEON_NOW_IN_EDIT_MODE)
        return true
    }
}