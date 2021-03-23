package it.forgottenworld.dungeons.core.command.edit.helpers


import com.google.inject.Inject
import com.google.inject.Singleton
import it.forgottenworld.dungeons.api.game.dungeon.DungeonManager
import it.forgottenworld.dungeons.api.math.Vector3i
import it.forgottenworld.dungeons.core.storage.Strings
import it.forgottenworld.dungeons.core.utils.getTargetSolidBlock
import it.forgottenworld.dungeons.core.utils.sendPrefixedMessage
import org.bukkit.entity.Player

@Singleton
class DungeonBoxCommandHelper @Inject constructor(
    private val dungeonManager: DungeonManager
) {

    fun setDungeonBoxPos(sender: Player, posNo: Int) {
        val block = sender.getTargetSolidBlock() ?: run {
            sender.sendPrefixedMessage(Strings.YOU_NEED_TO_BE_TARGETING)
            return
        }

        val dungeon = dungeonManager.getPlayerEditableDungeon(sender.uniqueId) ?: run {
            sender.sendPrefixedMessage(Strings.NOT_EDITING_ANY_DUNGEONS)
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
            sender.sendPrefixedMessage(
                Strings.DUNGEON_BOX_POS_SET,
                if (posNo == 1) Strings.FIRST else Strings.SECOND,
                if (posNo == 1) 2 else 1
            )
            return
        }

        dungeon.box = box.withOriginZero()

        dungeon.finalInstanceLocations.add(box.origin)
        dungeon.setupTestBox()

        sender.sendPrefixedMessage(Strings.DUNGEON_BOX_SET)
    }
}