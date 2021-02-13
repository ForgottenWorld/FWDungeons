package it.forgottenworld.dungeons.game.detection

import it.forgottenworld.dungeons.game.box.Box
import it.forgottenworld.dungeons.game.interactiveregion.Trigger
import it.forgottenworld.dungeons.utils.euclideanMod
import org.bukkit.util.BlockVector

object CubeGridUtils {

    private fun alignVectorToGrid(blockVector: BlockVector): BlockVector {
        val x = blockVector.blockX - (blockVector.blockX euclideanMod GRID_CUBE_SIZE_X)
        val y = blockVector.blockY - (blockVector.blockY euclideanMod GRID_CUBE_SIZE_Y)
        val z = blockVector.blockZ - (blockVector.blockZ euclideanMod GRID_CUBE_SIZE_Z)
        return BlockVector(x,y,z)
    }

    private fun tessellateAroundBox(box: Box): List<GridCubeIndex> {
        val origin = box.origin
        val opposite = box.originOpposite
        val x1 = origin.blockX - (origin.blockX euclideanMod GRID_CUBE_SIZE_X)
        val y1 = origin.blockY - (origin.blockY euclideanMod GRID_CUBE_SIZE_Y)
        val z1 = origin.blockZ - (origin.blockZ euclideanMod GRID_CUBE_SIZE_Z)
        val x2 = opposite.blockX - (opposite.blockX euclideanMod GRID_CUBE_SIZE_X)
        val y2 = opposite.blockY - (opposite.blockY euclideanMod GRID_CUBE_SIZE_Y)
        val z2 = opposite.blockZ - (opposite.blockZ euclideanMod GRID_CUBE_SIZE_Z)
        val res = mutableListOf<GridCubeIndex>()
        for (x in x1..x2 step GRID_CUBE_SIZE_X) {
            for (y in y1..y2 step GRID_CUBE_SIZE_Y) {
                for (z in z1..z2 step GRID_CUBE_SIZE_Z) {
                    res.add(GridCubeIndex(x,y,z))
                }
            }
        }
        return res
    }

    fun mapTriggersOntoGrid(
        indexes: List<GridCubeIndex>,
        triggers: Map<Int, Trigger>
    ): Map<GridCubeIndex, List<Int>> {
        val boxes = indexes.associateWith { it.box }
        val res = mutableMapOf<GridCubeIndex, List<Int>>()
        for ((id,trig) in triggers) {
            for (ind in indexes) {
               if (trig.box.intersects(boxes[ind]!!)) {
                   res[ind] = res[ind]?.plus(id) ?: listOf(id)
               }
            }
        }
        return res
    }

    fun lookupPosition(
        position: BlockVector,
        grid: Map<GridCubeIndex, List<Int>>,
        triggers: Map<Int, Trigger>
    ): Int? {
        val aligned = alignVectorToGrid(position).toCubeGridIndex()
        return grid[aligned]?.find {
            triggers[it]?.containsVector(position) == true
        }
    }

    const val GRID_CUBE_SIZE_X = 16
    const val GRID_CUBE_SIZE_Y = 16
    const val GRID_CUBE_SIZE_Z = 16
}