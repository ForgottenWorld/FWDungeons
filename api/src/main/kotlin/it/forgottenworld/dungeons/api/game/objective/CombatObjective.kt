package it.forgottenworld.dungeons.api.game.objective

import it.forgottenworld.dungeons.api.game.dungeon.instance.DungeonInstance
import java.util.*

interface CombatObjective {
    val instance: DungeonInstance
    var onAllKilled: (DungeonInstance) -> Unit
    var aborting: Boolean
    fun onMobKilled(uuid: UUID)
    fun abort()
}