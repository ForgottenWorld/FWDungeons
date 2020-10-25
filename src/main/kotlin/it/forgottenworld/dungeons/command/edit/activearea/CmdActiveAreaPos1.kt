package it.forgottenworld.dungeons.command.edit.activearea

import it.forgottenworld.dungeons.manager.DungeonEditManager
import it.forgottenworld.dungeons.model.ActiveArea
import it.forgottenworld.dungeons.model.Box
import it.forgottenworld.dungeons.utils.sendFWDMessage
import it.forgottenworld.dungeons.utils.targetBlock
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.util.BlockVector

fun cmdActiveAreaPos1(sender: Player, args: Array<out String>): Boolean {
    val block = sender.targetBlock

    if (block.blockData.material == Material.AIR) {
        sender.sendFWDMessage("You need to be targeting a block within 5 blocks of you before calling this")
        return true
    }

    val dungeon = DungeonEditManager.dungeonEditors[sender.uniqueId] ?: run {
        sender.sendFWDMessage("You're not editing any dungeons")
        return true
    }

    if (!DungeonEditManager.wipDungeons.contains(dungeon)) {
        sender.sendFWDMessage("This dungeon was already exported beforehand")
        return true
    }

    if (!dungeon.hasBox) {
        sender.sendFWDMessage("Dungeon box should be set before adding active areas")
        return true
    }

    var wipOrigin: BlockVector? = null
    if (DungeonEditManager.wipDungeonOrigins[sender.uniqueId]?.let {
                wipOrigin = it
                dungeon.box.withOrigin(it).containsBlock(block)
            } != true) {
        sender.sendFWDMessage("Target is not inside the dungeon box")
        return true
    }

    DungeonEditManager.wipActiveAreaPos2s[sender.uniqueId]?.let { p2 ->
        val id = (dungeon.activeAreas.maxByOrNull { it.id }?.id?.plus(1)) ?: 0
        val box = Box(block, p2)
        dungeon.activeAreas.add(
                ActiveArea(
                        id,
                        Box(block, p2).withContainerOrigin(wipOrigin!!, BlockVector(0,0,0))
                )
        )
        DungeonEditManager.wipTestInstances[sender.uniqueId]?.run {
            activeAreas.add(ActiveArea(
                    id,
                    box
            ))
            updateHlBlocks()
        }
        box.highlightAll()
        DungeonEditManager.wipActiveAreaPos2s.remove(sender.uniqueId)
        sender.sendFWDMessage("Created active area with id $id")
    } ?: run {
        DungeonEditManager.wipActiveAreaPos1s[sender.uniqueId] = block
        sender.sendFWDMessage("First position set, now pick another with /fwde activearea pos2")
    }

    return true
}