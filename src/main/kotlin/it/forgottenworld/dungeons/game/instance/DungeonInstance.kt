package it.forgottenworld.dungeons.game.instance

import it.forgottenworld.dungeons.game.box.Box
import it.forgottenworld.dungeons.game.dungeon.Dungeon
import it.forgottenworld.dungeons.game.interactiveregion.ActiveArea
import it.forgottenworld.dungeons.game.interactiveregion.Trigger
import it.forgottenworld.dungeons.utils.Vector3i

interface DungeonInstance {
    val id: Int
    val box: Box
    val dungeon: Dungeon
    val origin: Vector3i
    val triggers: Map<Int, Trigger>
    val activeAreas: Map<Int, ActiveArea>
}