package it.forgottenworld.dungeons.game.detection

import it.forgottenworld.dungeons.game.box.Box
import it.forgottenworld.dungeons.game.dungeon.FinalDungeon
import it.forgottenworld.dungeons.game.interactiveregion.Trigger
import it.forgottenworld.dungeons.utils.NestableGrid3iToNi
import it.forgottenworld.dungeons.utils.Vector3i
import it.forgottenworld.dungeons.utils.cubeWithSide
import kotlin.properties.ReadOnlyProperty

object CubeGridUtils {

    private fun tessellateAroundBox(box: Box): NestableGrid3iToNi {
        val opposite = box.originOpposite
        return NestableGrid3iToNi(
            opposite.x + GRID_INITIAL_CELL_SIZE,
            opposite.y + GRID_INITIAL_CELL_SIZE,
            opposite.z + GRID_INITIAL_CELL_SIZE,
            GRID_INITIAL_CELL_SIZE
        )
    }

    private fun mapTriggersOntoGrid(
        grid: NestableGrid3iToNi,
        triggers: Map<Int, Trigger>,
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
            if (v.size > 1 && nestingLevel < 2) {
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
        triggers: Map<Int, Trigger>
    ) = grid[x, y, z]?.find {
        triggers[it]?.containsXYZ(x,y,z) == true
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
        triggers: Map<Int, Trigger>
    ) = lookupTriggersForPosition(x, y, z, this, triggers)

    const val GRID_INITIAL_CELL_SIZE = 16
}