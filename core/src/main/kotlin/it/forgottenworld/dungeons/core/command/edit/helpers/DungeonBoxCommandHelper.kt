package it.forgottenworld.dungeons.core.command.edit.helpers


import it.forgottenworld.dungeons.api.math.Vector3i
import it.forgottenworld.dungeons.core.config.Strings
import it.forgottenworld.dungeons.core.game.DungeonManager.editableDungeon
import it.forgottenworld.dungeons.core.utils.getTargetSolidBlock
import it.forgottenworld.dungeons.core.utils.sendFWDMessage
import org.bukkit.Material
import org.bukkit.entity.Player

object DungeonBoxCommandHelper {

    fun setDungeonBoxPos(sender: Player, posNo: Int) {
        val block = sender.getTargetSolidBlock()

        if (block.blockData.material == Material.AIR) {
            sender.sendFWDMessage(Strings.YOU_NEED_TO_BE_TARGETING)
            return
        }

        val dungeon = sender.uniqueId.editableDungeon ?: run {
            sender.sendFWDMessage(Strings.NOT_EDITING_ANY_DUNGEONS)
            return
        }

        val builder = dungeon.dungeonBoxBuilder
        if (posNo == 1) {
            builder.pos1(Vector3i.ofBlock(block))
        } else {
            builder.pos2(Vector3i.ofBlock(block))
        }

        val box = builder.build()
        if (box == null) {
            sender.sendFWDMessage(
                Strings.DUNGEON_BOX_POS_SET.format(
                    if (posNo == 1) Strings.FIRST else Strings.SECOND,
                    if (posNo == 1) 2 else 1
                )
            )
            return
        }

        dungeon.box = box.withOriginZero()

        dungeon.finalInstanceLocations.add(box.origin)
        dungeon.setupTestBox()

        sender.sendFWDMessage(Strings.DUNGEON_BOX_SET)
    }
}