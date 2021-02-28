package it.forgottenworld.dungeons.core.command.edit.dungeon

import it.forgottenworld.dungeons.api.command.PlayerCommand
import it.forgottenworld.dungeons.api.math.Vector3i
import it.forgottenworld.dungeons.core.config.Strings
import it.forgottenworld.dungeons.core.game.dungeon.DungeonManager
import it.forgottenworld.dungeons.core.utils.sendFWDMessage
import it.forgottenworld.dungeons.core.utils.getTargetSolidBlock
import org.bukkit.Material
import org.bukkit.entity.Player

class CmdDungeonImport : PlayerCommand() {

    override fun command(sender: Player, args: Array<out String>): Boolean {
        if (args.isEmpty()) {
            sender.sendFWDMessage(Strings.NEA_PROVIDE_DUNGEON_ID)
            return true
        }

        val id = args[0].toIntOrNull()
        if (id == null) {
            sender.sendFWDMessage(Strings.DUNGEON_ID_SHOULD_BE_INT)
            return true
        }

        val block = sender.getTargetSolidBlock()
        if (block.blockData.material == Material.AIR) {
            sender.sendFWDMessage(Strings.YOU_NEED_TO_BE_TARGETING)
            return true
        }

        val dungeon = DungeonManager.finalDungeons[id] ?: run {
            sender.sendFWDMessage(Strings.NO_DUNGEON_FOUND_WITH_ID.format(id))
            return true
        }

        if (!dungeon.import(Vector3i.ofBlock(block))) {
            sender.sendFWDMessage(Strings.THIS_DUNGEON_ALREADY_HAS_INSTANCES)
            return true
        }

        sender.sendFWDMessage(Strings.DUNGEON_IMPORTED)
        return true
    }
}