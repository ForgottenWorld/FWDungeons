package it.forgottenworld.dungeons.command.edit.dungeon

import it.forgottenworld.dungeons.command.api.CommandHandler
import it.forgottenworld.dungeons.config.Strings
import it.forgottenworld.dungeons.game.dungeon.EditableDungeon.Companion.editableDungeon
import it.forgottenworld.dungeons.utils.sendFWDMessage
import it.forgottenworld.dungeons.utils.targetBlock
import it.forgottenworld.dungeons.utils.vector3i
import org.bukkit.Material
import org.bukkit.entity.Player

class CmdDungeonInstanceAdd: CommandHandler<Player> {

    override fun command(sender: Player, args: Array<out String>): Boolean {
        val block = sender.targetBlock

        if (block.blockData.material == Material.AIR) {
            sender.sendFWDMessage(Strings.YOU_NEED_TO_BE_TARGETING)
            return true
        }

        val dungeon = sender.editableDungeon ?: run {
            sender.sendFWDMessage(Strings.NOT_EDITING_ANY_DUNGEONS)
            return true
        }

        dungeon.finalInstanceLocations.add(block.vector3i)

        sender.sendFWDMessage(Strings.INSTANCE_ADDED)

        return true
    }
}