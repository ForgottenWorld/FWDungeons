package it.forgottenworld.dungeons.command.edit.activearea

import it.forgottenworld.dungeons.cli.InteractiveRegionListGui
import it.forgottenworld.dungeons.command.api.CommandHandler
import it.forgottenworld.dungeons.config.Strings
import it.forgottenworld.dungeons.game.dungeon.EditableDungeon.Companion.editableDungeon
import it.forgottenworld.dungeons.utils.sendFWDMessage
import org.bukkit.entity.Player

class CmdActiveAreaList : CommandHandler<Player> {

    override fun command(sender: Player, args: Array<out String>): Boolean {
        val dungeon = sender.editableDungeon ?: run {
            sender.sendFWDMessage(Strings.NOT_EDITING_ANY_DUNGEONS)
            return true
        }

        sender.spigot()
            .sendMessage(*InteractiveRegionListGui.showActiveAreas(dungeon, args.getOrNull(0)?.toIntOrNull() ?: 0))
        return true
    }
}