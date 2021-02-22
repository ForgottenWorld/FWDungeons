package it.forgottenworld.dungeons.core.command.edit.dungeon

import it.forgottenworld.dungeons.api.command.PlayerCommand
import it.forgottenworld.dungeons.core.config.Strings
import it.forgottenworld.dungeons.core.game.dungeon.DungeonManager.editableDungeon
import it.forgottenworld.dungeons.core.utils.sendFWDMessage
import it.forgottenworld.dungeons.core.utils.targetBlock
import it.forgottenworld.dungeons.core.utils.vector3i
import org.bukkit.Material
import org.bukkit.entity.Player

class CmdDungeonInstanceAdd: PlayerCommand() {

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