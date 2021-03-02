package it.forgottenworld.dungeons.core.game.detection

import com.google.inject.Singleton
import it.forgottenworld.dungeons.api.game.interactiveregion.Trigger
import it.forgottenworld.dungeons.api.math.NestableGrid3iToNi

@Singleton
class TriggerChecker {

    private fun lookupTriggersForPosition(
        x: Int, y: Int, z: Int,
        grid: NestableGrid3iToNi,
        triggers: Map<Int, Trigger>
    ): Trigger? {
        for (id in grid[x,y,z] ?: return null) {
            val trig = triggers[id]!!
            if (trig.containsXYZ(x,y,z)) return trig
        }
        return null
    }

    fun checkPositionAgainstTriggers(
        grid: NestableGrid3iToNi,
        x: Int, y: Int, z: Int,
        triggers: Map<Int, Trigger>
    ) = lookupTriggersForPosition(x, y, z, grid, triggers)
}