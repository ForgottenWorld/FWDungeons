package it.forgottenworld.dungeons.core.command.edit.dungeon

import com.google.inject.Inject
import it.forgottenworld.dungeons.api.command.PlayerCommand
import it.forgottenworld.dungeons.api.math.Vector3i
import it.forgottenworld.dungeons.core.config.Strings
import it.forgottenworld.dungeons.core.game.DungeonManager
import it.forgottenworld.dungeons.core.utils.getTargetSolidBlock
import it.forgottenworld.dungeons.core.utils.sendFWDMessage
import org.bukkit.Material
import org.bukkit.entity.Player

class CmdDungeonInstanceAdd @Inject constructor(
    private val dungeonManager: DungeonManager
) : PlayerCommand() {

    override fun command(sender: Player, args: Array<out String>): Boolean {
        val block = sender.getTargetSolidBlock()

        if (block.blockData.material == Material.AIR) {
            sender.sendFWDMessage(Strings.YOU_NEED_TO_BE_TARGETING)
            return true
        }

        val dungeon = dungeonManager.getPlayerEditableDungeon(sender.uniqueId) ?: run {
            sender.sendFWDMessage(Strings.NOT_EDITING_ANY_DUNGEONS)
            return true
        }

        dungeon.finalInstanceLocations.add(Vector3i.ofBlock(block))

        sender.sendFWDMessage(Strings.INSTANCE_ADDED)

        return true
    }
}