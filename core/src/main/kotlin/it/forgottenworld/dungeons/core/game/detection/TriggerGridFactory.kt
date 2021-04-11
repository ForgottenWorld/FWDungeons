package it.forgottenworld.dungeons.core.game.detection

import com.google.inject.Singleton
import it.forgottenworld.dungeons.api.game.dungeon.FinalDungeon
import it.forgottenworld.dungeons.api.game.dungeon.subelement.interactiveregion.Trigger
import it.forgottenworld.dungeons.api.math.Box
import it.forgottenworld.dungeons.api.math.NestableGrid3iToNi
import it.forgottenworld.dungeons.api.math.Vector3i
import it.forgottenworld.dungeons.core.utils.sendConsoleMessage

@Singleton
class TriggerGridFactory {

    private fun createGridForBox(box: Box): NestableGrid3iToNi {
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
        triggers: Map<Int, Trigger>,
        nestingLevel: Int = 0
    ) {
        val indices = grid.indices
        val boxes = indices.associateWith { it.cubeWithSide(grid.cellSize) }
        val vectorMap = mutableMapOf<Vector3i, IntArray>()
        for ((id, trig) in triggers) {
            for ((ind, box) in boxes) {
                if (trig.box.intersects(box)) {
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

    fun createFinalDungeonGrid(
        finalDungeon: FinalDungeon
    ): NestableGrid3iToNi {
        sendConsoleMessage(" -- Calculating trigger grid for §5${finalDungeon.name}")
        val grid = createGridForBox(finalDungeon.box)
        mapTriggersOntoGrid(grid, finalDungeon.triggers)
        return grid
    }

    companion object {
        private const val GRID_INITIAL_CELL_SIZE = 16
    }
}