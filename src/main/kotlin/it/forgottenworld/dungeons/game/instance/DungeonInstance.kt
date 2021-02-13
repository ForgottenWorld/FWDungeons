package it.forgottenworld.dungeons.game.instance

import it.forgottenworld.dungeons.game.box.Box
import it.forgottenworld.dungeons.game.dungeon.Dungeon
import it.forgottenworld.dungeons.game.interactiveregion.ActiveArea
import it.forgottenworld.dungeons.game.interactiveregion.Trigger
import org.bukkit.util.BlockVector

interface DungeonInstance {
    val id: Int
    val box: Box
    val dungeon: Dungeon
    val origin: BlockVector
    val triggers: Map<Int, Trigger>
    val activeAreas: Map<Int, ActiveArea>
}