package it.forgottenworld.dungeons.manager

import it.forgottenworld.dungeons.manager.DungeonEditManager.dungeonBoxBuilder
import it.forgottenworld.dungeons.utils.ktx.blockVector
import it.forgottenworld.dungeons.utils.ktx.sendFWDMessage
import it.forgottenworld.dungeons.utils.ktx.targetBlock
import org.bukkit.Material
import org.bukkit.entity.Player

object DungeonBoxCreationManager {

    fun setDungeonBoxPos(sender: Player, posNo: Int) {
        val block = sender.targetBlock

        if (block.blockData.material == Material.AIR) {
            sender.sendFWDMessage("You need to be targeting a block within 5 blocks of you before calling this")
            return
        }

        val dungeon = DungeonEditManager.wipDungeons[sender.uniqueId] ?: run {
            sender.sendFWDMessage("You're not editing any dungeons")
            return
        }

        val builder = sender.dungeonBoxBuilder
        if (posNo == 1)
            builder.pos1(block.blockVector)
        else
            builder.pos2(block.blockVector)

        val box = builder.build()
        if (box == null) {
            sender.sendFWDMessage("${
                if (posNo == 1) 
                    "First" else "Second"
            } position set, now pick another with /fwde dungeon pos${
                if (posNo == 1) 
                    "2" else "1"}")
            return
        }

        dungeon.box = box.withOriginZero()
        DungeonEditManager.dungeonBoxBuilders.remove(sender.uniqueId)

        dungeon.createTestInstance(box.origin, sender)

        sender.sendFWDMessage("Dungeon box set")
    }
}