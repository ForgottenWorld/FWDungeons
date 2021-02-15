package it.forgottenworld.dungeons.command.edit.helpers


import it.forgottenworld.dungeons.config.Strings
import it.forgottenworld.dungeons.game.dungeon.EditableDungeon.Companion.editableDungeon
import it.forgottenworld.dungeons.utils.sendFWDMessage
import it.forgottenworld.dungeons.utils.targetBlock
import it.forgottenworld.dungeons.utils.vector3i
import org.bukkit.Material
import org.bukkit.entity.Player

object DungeonBoxCommandHelper {

    fun setDungeonBoxPos(sender: Player, posNo: Int) {
        val block = sender.targetBlock

        if (block.blockData.material == Material.AIR) {
            sender.sendFWDMessage(Strings.YOU_NEED_TO_BE_TARGETING)
            return
        }

        val dungeon = sender.editableDungeon ?: run {
            sender.sendFWDMessage(Strings.NOT_EDITING_ANY_DUNGEONS)
            return
        }

        val builder = dungeon.dungeonBoxBuilder
        if (posNo == 1)
            builder.pos1(block.vector3i)
        else
            builder.pos2(block.vector3i)

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
        dungeon.createTestInstance()

        sender.sendFWDMessage(Strings.DUNGEON_BOX_SET)
    }
}