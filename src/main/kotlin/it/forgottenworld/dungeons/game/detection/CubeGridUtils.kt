package it.forgottenworld.dungeons.game.detection

import it.forgottenworld.dungeons.game.box.Box
import it.forgottenworld.dungeons.game.dungeon.FinalDungeon
import it.forgottenworld.dungeons.game.interactiveregion.Trigger
import it.forgottenworld.dungeons.utils.NestableGrid3iToNiPos
import it.forgottenworld.dungeons.utils.Vector3i
import it.forgottenworld.dungeons.utils.box
import it.forgottenworld.dungeons.utils.euclideanMod
import kotlin.properties.ReadOnlyProperty

object CubeGridUtils {

    private fun alignVectorToGrid(vector: Vector3i): Vector3i {
        val x = vector.x - (vector.x euclideanMod this.GRID_INITIAL_CELL_SIZE)
        val y = vector.y - (vector.y euclideanMod this.GRID_INITIAL_CELL_SIZE)
        val z = vector.z - (vector.z euclideanMod this.GRID_INITIAL_CELL_SIZE)
        return Vector3i(x, y, z)
    }

    private fun tessellateAroundBox(box: Box): NestableGrid3iToNiPos {
        val opposite = box.originOpposite
        val x2 = opposite.x - (opposite.x euclideanMod GRID_INITIAL_CELL_SIZE)
        val y2 = opposite.y - (opposite.y euclideanMod GRID_INITIAL_CELL_SIZE)
        val z2 = opposite.z - (opposite.z euclideanMod GRID_INITIAL_CELL_SIZE)
        return NestableGrid3iToNiPos(
            x2 + GRID_INITIAL_CELL_SIZE,
            y2 + GRID_INITIAL_CELL_SIZE,
            z2 + GRID_INITIAL_CELL_SIZE,
            GRID_INITIAL_CELL_SIZE
        )
    }

    private fun mapTriggersOntoGrid(
        grid: NestableGrid3iToNiPos,
        triggers: Map<Int, Trigger>,
        nestingLevel: Int = 0
    ) {
        val indices = grid.indices
        val boxes = indices.associateWith { it.box }
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
        grid: NestableGrid3iToNiPos,
        triggers: Map<Int, Trigger>
    ) = grid[x, y, z]?.find {
        triggers[it]?.containsXYZ(x,y,z) == true
    }

    private fun createFinalDungeonGrid(
        finalDungeon: FinalDungeon
    ): NestableGrid3iToNiPos {
        val grid = tessellateAroundBox(finalDungeon.box)
        mapTriggersOntoGrid(grid, finalDungeon.triggers)
        return grid
    }

    fun FinalDungeon.triggerGrid(): ReadOnlyProperty<FinalDungeon, NestableGrid3iToNiPos> {
        val grid = createFinalDungeonGrid(this)
        return ReadOnlyProperty { _, _ -> grid }
    }

    fun NestableGrid3iToNiPos.checkPositionAgainstTriggers(
        x: Int,
        y: Int,
        z: Int,
        triggers: Map<Int, Trigger>
    ) = lookupTriggersForPosition(x, y, z, this, triggers)

    const val GRID_INITIAL_CELL_SIZE = 16
}