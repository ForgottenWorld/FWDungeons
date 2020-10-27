package it.forgottenworld.dungeons.model.instance

import it.forgottenworld.dungeons.model.box.Box
import it.forgottenworld.dungeons.model.dungeon.Dungeon
import it.forgottenworld.dungeons.model.interactiveelement.ActiveArea
import it.forgottenworld.dungeons.model.interactiveelement.Trigger
import org.bukkit.util.BlockVector

interface DungeonInstance {
    val id: Int
    val box: Box
    val dungeon: Dungeon
    val origin: BlockVector
    val triggers: Map<Int, Trigger>
    val activeAreas: Map<Int, ActiveArea>
}