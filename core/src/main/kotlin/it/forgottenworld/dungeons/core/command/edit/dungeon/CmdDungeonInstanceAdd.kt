package it.forgottenworld.dungeons.core.command.edit.dungeon

import it.forgottenworld.dungeons.api.command.PlayerCommand
import it.forgottenworld.dungeons.api.math.Vector3i
import it.forgottenworld.dungeons.core.config.Strings
import it.forgottenworld.dungeons.core.game.DungeonManager.editableDungeon
import it.forgottenworld.dungeons.core.utils.sendFWDMessage
import it.forgottenworld.dungeons.core.utils.getTargetSolidBlock
import org.bukkit.Material
import org.bukkit.entity.Player

class CmdDungeonInstanceAdd: PlayerCommand() {

    override fun command(sender: Player, args: Array<out String>): Boolean {
        val block = sender.getTargetSolidBlock()

        if (block.blockData.material == Material.AIR) {
            sender.sendFWDMessage(Strings.YOU_NEED_TO_BE_TARGETING)
            return true
        }

        val dungeon = sender.uniqueId.editableDungeon ?: run {
            sender.sendFWDMessage(Strings.NOT_EDITING_ANY_DUNGEONS)
            return true
        }

        dungeon.finalInstanceLocations.add(Vector3i.ofBlock(block))

        sender.sendFWDMessage(Strings.INSTANCE_ADDED)

        return true
    }
}