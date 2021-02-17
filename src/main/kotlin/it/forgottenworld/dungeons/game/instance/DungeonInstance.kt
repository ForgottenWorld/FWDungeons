package it.forgottenworld.dungeons.game.instance

import it.forgottenworld.dungeons.game.box.Box
import it.forgottenworld.dungeons.game.dungeon.Dungeon
import it.forgottenworld.dungeons.utils.Vector3i

interface DungeonInstance {
    val id: Int
    val box: Box
    val dungeon: Dungeon
    val origin: Vector3i
}