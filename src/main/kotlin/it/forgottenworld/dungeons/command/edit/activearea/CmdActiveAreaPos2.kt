package it.forgottenworld.dungeons.command.edit.activearea

import it.forgottenworld.dungeons.model.activearea.ActiveArea
import it.forgottenworld.dungeons.model.box.Box
import it.forgottenworld.dungeons.state.DungeonEditState
import it.forgottenworld.dungeons.utils.sendFWDMessage
import it.forgottenworld.dungeons.utils.targetBlock
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.util.BlockVector

fun cmdActiveAreaPos2(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
    if (sender !is Player) return true
    val block = sender.targetBlock

    if (block.blockData.material == Material.AIR) {
        sender.sendFWDMessage("You need to be targeting a block within 5 blocks of you before calling this")
        return true
    }

    val dungeon = DungeonEditState.dungeonEditors[sender.uniqueId] ?: run {
        sender.sendFWDMessage("You're not editing any dungeons")
        return true
    }

    if (!DungeonEditState.wipDungeons.contains(dungeon)) {
        sender.sendFWDMessage("This dungeon was already exported beforehand")
        return true
    }

    if (!dungeon.hasBox) {
        sender.sendFWDMessage("Dungeon box should be set before adding active areas")
        return true
    }

    var wipOrigin: BlockVector? = null
    if (DungeonEditState.wipDungeonOrigins[sender.uniqueId]?.let {
                wipOrigin = it
                dungeon.box.withOrigin(it).containsBlock(block)
            } != true) {
        sender.sendFWDMessage("Target is not inside the dungeon box")
        return true
    }

    DungeonEditState.wipActiveAreaPos1s[sender.uniqueId]?.let { p1 ->
        val id = (dungeon.activeAreas.maxByOrNull { it.id }?.id?.plus(1)) ?: 0
        val box = Box(p1, block)
        dungeon.activeAreas.add(
                ActiveArea(
                        id,
                        Box(p1, block).withContainerOrigin(wipOrigin!!, BlockVector(0,0,0))
                )
        )
        DungeonEditState.wipTestInstances[sender.uniqueId]?.run {
            activeAreas.add(ActiveArea(
                    id,
                    box
            ))
            updateHlBlocks()
        }
        box.highlightAll()
        DungeonEditState.wipActiveAreaPos1s.remove(sender.uniqueId)
        sender.sendFWDMessage("Created active area with id $id")
    } ?: ({
        DungeonEditState.wipActiveAreaPos2s[sender.uniqueId] = block
        sender.sendFWDMessage("First position set, now pick another with /fwde activearea pos1")
    })()

    return true
}