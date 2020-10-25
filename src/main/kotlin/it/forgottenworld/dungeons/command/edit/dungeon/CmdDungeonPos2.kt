package it.forgottenworld.dungeons.command.edit.dungeon

import it.forgottenworld.dungeons.model.Box
import it.forgottenworld.dungeons.model.DungeonInstance
import it.forgottenworld.dungeons.manager.DungeonEditManager
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
    
    val dungeon = DungeonEditManager.dungeonEditors[sender.uniqueId] ?: run {
        sender.sendFWDMessage("You're not editing any dungeons")
        return true
    }

    DungeonEditManager.wipDungeonPos1s[sender.uniqueId]?.let {
        dungeon.box = Box(it, block).withOriginZero()
        val newOrigin = minBlockVector(block, it)
        DungeonEditManager.wipDungeonOrigins[sender.uniqueId] = newOrigin
        dungeon.instances.clear()
        DungeonEditManager.wipTestInstances[sender.uniqueId] =
                DungeonInstance(
                        1000,
                        dungeon,
                        newOrigin,
                        mutableMapOf(),
                        mutableListOf(),
                        true
                ).apply {
                    dungeon.instances.add(this)
                    tester = sender
                }
        DungeonEditManager.wipDungeonPos1s.remove(sender.uniqueId)
        sender.sendFWDMessage("Dungeon box set")
    } ?: ({
        DungeonEditManager.wipDungeonPos2s[sender.uniqueId] = block
        sender.sendFWDMessage("First position set, now pick another with /fwde dungeon pos1")
    })()

    return true
}