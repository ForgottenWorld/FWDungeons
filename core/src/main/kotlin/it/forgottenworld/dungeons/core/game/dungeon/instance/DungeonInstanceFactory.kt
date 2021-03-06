package it.forgottenworld.dungeons.core.game.dungeon.instance

import it.forgottenworld.dungeons.api.game.dungeon.instance.DungeonInstance
import it.forgottenworld.dungeons.api.math.Vector3i
import it.forgottenworld.dungeons.api.game.dungeon.FinalDungeon

interface DungeonInstanceFactory {
    fun create(id: Int, dungeon: FinalDungeon, origin: Vector3i): DungeonInstance
    fun create(dungeon: FinalDungeon, origin: Vector3i): DungeonInstance
}