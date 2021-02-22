package it.forgottenworld.dungeons.core.game.detection

import it.forgottenworld.dungeons.api.math.Box
import it.forgottenworld.dungeons.api.math.NestableGrid3iToNi
import it.forgottenworld.dungeons.api.math.Vector3i
import it.forgottenworld.dungeons.core.game.dungeon.FinalDungeon
import it.forgottenworld.dungeons.core.game.interactiveregion.TriggerImpl
import it.forgottenworld.dungeons.core.utils.cubeWithSide
import kotlin.properties.ReadOnlyProperty

object CubeGridFactory {

    private fun tessellateAroundBox(box: Box): NestableGrid3iToNi {
        val opposite = box.getOriginOpposite()
        return NestableGrid3iToNi(
            opposite.x,
            opposite.y,
            opposite.z,
            GRID_INITIAL_CELL_SIZE
        )
    }

    private fun mapTriggersOntoGrid(
        grid: NestableGrid3iToNi,
        triggers: Map<Int, TriggerImpl>,
        nestingLevel: Int = 0
    ) {
        val indices = grid.indices
        val boxes = indices.associateWith { it.cubeWithSide(grid.cellSize) }
        val vectorMap = mutableMapOf<Vector3i, IntArray>()
        for ((id, trig) in triggers) {
            for (ind in indices) {
                if (trig.box.intersects(boxes[ind]!!)) {
                    vectorMap[ind] = vectorMap[ind]?.plus(id) ?: intArrayOf(id)
                }
            }
        }
        for ((k,v) in vectorMap) {
            if (v.size > 2 && nestingLevel < 2) {
                val nestedGrid = grid.nestAt(k.x,k.y,k.z)
                val trigs = triggers.filterKeys { v.contains(it) }
                mapTriggersOntoGrid(nestedGrid, trigs, nestingLevel + 1)
            } else {
                grid[k.x,k.y,k.z] = v
            }
        }
    }

    private fun lookupTriggersForPosition(
        x: Int,
        y: Int,
        z: Int,
        grid: NestableGrid3iToNi,
        triggers: Map<Int, TriggerImpl>
    ): TriggerImpl? {
        for (id in grid[x,y,z] ?: return null) {
            val trig = triggers[id]!!
            if (trig.containsXYZ(x,y,z)) return trig
        }
        return null
    }

    private fun createFinalDungeonGrid(
        finalDungeon: FinalDungeon
    ): NestableGrid3iToNi {
        val grid = tessellateAroundBox(finalDungeon.box)
        mapTriggersOntoGrid(grid, finalDungeon.triggers)
        return grid
    }

    fun FinalDungeon.triggerGrid(): ReadOnlyProperty<FinalDungeon, NestableGrid3iToNi> {
        val grid = createFinalDungeonGrid(this)
        return ReadOnlyProperty { _, _ -> grid }
    }

    fun NestableGrid3iToNi.checkPositionAgainstTriggers(
        x: Int,
        y: Int,
        z: Int,
        triggers: Map<Int, TriggerImpl>
    ) = lookupTriggersForPosition(x, y, z, this, triggers)

    private const val GRID_INITIAL_CELL_SIZE = 16
}