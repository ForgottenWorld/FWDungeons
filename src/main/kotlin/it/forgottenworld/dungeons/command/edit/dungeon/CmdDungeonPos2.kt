package it.forgottenworld.dungeons.command.edit.dungeon

import it.forgottenworld.dungeons.model.box.Box
import it.forgottenworld.dungeons.model.dungeon.DungeonInstance
import it.forgottenworld.dungeons.state.DungeonEditState
import it.forgottenworld.dungeons.utils.minBlockVector
import it.forgottenworld.dungeons.utils.sendFWDMessage
import it.forgottenworld.dungeons.utils.targetBlock
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

fun cmdDungeonPos2(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
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

    DungeonEditState.wipDungeonPos1s[sender.uniqueId]?.let {
        dungeon.box = Box(it, block).withOriginZero()
        val newOrigin = minBlockVector(block, it)
        DungeonEditState.wipDungeonOrigins[sender.uniqueId] = newOrigin
        dungeon.instances.clear()
        DungeonEditState.wipTestInstances[sender.uniqueId] =
                DungeonInstance(
                        1000,
                        dungeon,
                        newOrigin,
                        mutableMapOf(),
                        mutableListOf()
                ).apply { dungeon.instances.add(this) }
        DungeonEditState.wipDungeonPos1s.remove(sender.uniqueId)
        sender.sendFWDMessage("Dungeon box set")
    } ?: ({
        DungeonEditState.wipDungeonPos2s[sender.uniqueId] = block
        sender.sendFWDMessage("First position set, now pick another with /fwde dungeon pos1")
    })()

    return true
}