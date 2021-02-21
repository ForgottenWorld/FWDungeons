package it.forgottenworld.dungeons.api.game.instance

import it.forgottenworld.dungeons.api.game.dungeon.Dungeon
import it.forgottenworld.dungeons.api.math.Vector3i

interface DungeonInstance {
    val id: Int
    val dungeon: Dungeon
    val origin: Vector3i
}