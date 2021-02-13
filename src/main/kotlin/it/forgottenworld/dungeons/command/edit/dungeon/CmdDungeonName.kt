package it.forgottenworld.dungeons.command.edit.dungeon

import it.forgottenworld.dungeons.command.api.CommandHandler
import it.forgottenworld.dungeons.config.Strings
import it.forgottenworld.dungeons.game.dungeon.EditableDungeon.Companion.editableDungeon
import it.forgottenworld.dungeons.game.dungeon.FinalDungeon
import it.forgottenworld.dungeons.utils.sendFWDMessage
import org.bukkit.entity.Player

class CmdDungeonName : CommandHandler<Player> {

    override fun command(sender: Player, args: Array<out String>): Boolean {
        if (args.isEmpty()) {
            sender.sendFWDMessage(Strings.NEA_PROVIDE_NAME)
            return true
        }

        val name = args.joinToString(" ")

        val dungeon = sender.editableDungeon ?: run {
            sender.sendFWDMessage(Strings.NOT_EDITING_ANY_DUNGEONS)
            return true
        }

        if (FinalDungeon.dungeons.values.any { it.name.equals(name.trim(), true) }) {
            sender.sendFWDMessage(Strings.ANOTHER_DUNGEON_WITH_SAME_NAME_EXISTS)
            return true
        }

        dungeon.name = name
        sender.sendFWDMessage(Strings.DUNGEON_NAME_CHANGED)

        return true
    }
}