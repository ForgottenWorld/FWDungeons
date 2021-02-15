package it.forgottenworld.dungeons.command.edit.dungeon

import it.forgottenworld.dungeons.command.api.CommandHandler
import it.forgottenworld.dungeons.config.Strings
import it.forgottenworld.dungeons.game.dungeon.FinalDungeon
import it.forgottenworld.dungeons.utils.sendFWDMessage
import it.forgottenworld.dungeons.utils.targetBlock
import it.forgottenworld.dungeons.utils.vector3i
import org.bukkit.Material
import org.bukkit.entity.Player

class CmdDungeonImport : CommandHandler<Player> {

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

        val block = sender.targetBlock
        if (block.blockData.material == Material.AIR) {
            sender.sendFWDMessage(Strings.YOU_NEED_TO_BE_TARGETING)
            return true
        }

        val dungeon = FinalDungeon.dungeons[id] ?: run {
            sender.sendFWDMessage(Strings.NO_DUNGEON_FOUND_WITH_ID.format(id))
            return true
        }

        if (!dungeon.import(block.vector3i)) {
            sender.sendFWDMessage(Strings.THIS_DUNGEON_ALREADY_HAS_INSTANCES)
            return true
        }

        sender.sendFWDMessage(Strings.DUNGEON_IMPORTED)
        return true
    }
}