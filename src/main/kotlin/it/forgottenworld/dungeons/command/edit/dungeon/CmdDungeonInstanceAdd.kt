package it.forgottenworld.dungeons.command.edit.dungeon

import it.forgottenworld.dungeons.manager.DungeonEditManager
import it.forgottenworld.dungeons.utils.ktx.blockVector
import it.forgottenworld.dungeons.utils.ktx.firstMissing
import it.forgottenworld.dungeons.utils.ktx.sendFWDMessage
import it.forgottenworld.dungeons.utils.ktx.targetBlock
import org.bukkit.Material
import org.bukkit.entity.Player

fun cmdDungeonInstanceAdd(sender: Player, args: Array<out String>): Boolean {
    val block = sender.targetBlock

    if (block.blockData.material == Material.AIR) {
        sender.sendFWDMessage("You need to be targeting a block within 5 blocks of you before calling this")
        return true
    }

    val dungeon = DungeonEditManager.wipDungeons[sender.uniqueId] ?: run {
        sender.sendFWDMessage("You're not editing any dungeons")
        return true
    }

    val id = dungeon.finalInstanceLocations.keys.firstMissing()
    dungeon.finalInstanceLocations[id] = block.blockVector

    sender.sendFWDMessage("Created instance with id $id")

    return true
}