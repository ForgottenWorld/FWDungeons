package it.forgottenworld.dungeons.command.edit.dungeon

import it.forgottenworld.dungeons.model.dungeon.FinalDungeon
import it.forgottenworld.dungeons.utils.ktx.blockVector
import it.forgottenworld.dungeons.utils.ktx.sendFWDMessage
import it.forgottenworld.dungeons.utils.ktx.targetBlock
import org.bukkit.Material
import org.bukkit.entity.Player

fun cmdDungeonImport(sender: Player, args: Array<out String>): Boolean {
    if (args.isEmpty()) {
        sender.sendFWDMessage("Not enough arguments: please provide a dungeon id")
        return true
    }

    val id = args[0].toIntOrNull()
    if (id == null) {
        sender.sendFWDMessage("Dungeon id should be an integer")
        return true
    }

    val block = sender.targetBlock
    if (block.blockData.material == Material.AIR) {
        sender.sendFWDMessage("You need to be targeting a block within 5 blocks of you before calling this")
        return true
    }

    val dungeon = FinalDungeon.dungeons[id] ?: run {
        sender.sendFWDMessage("No dungeon found with id $id")
        return true
    }

    if (!dungeon.import(block.blockVector)) {
        sender.sendFWDMessage("This dungeon already has instances")
        return true
    }

    sender.sendFWDMessage("Dungeon imported")
    return true
}