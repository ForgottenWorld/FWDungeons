package it.forgottenworld.dungeons.game.detection

import it.forgottenworld.dungeons.game.box.Box
import org.bukkit.util.BlockVector

typealias GridCubeIndex = Triple<Int, Int, Int>

fun GridCubeIndex.toBlockVector() = BlockVector(first,second,third)

fun BlockVector.toCubeGridIndex() = GridCubeIndex(blockX,blockY,blockZ)

val GridCubeIndex.box get() = Box(
    toBlockVector(),
    CubeGridUtils.GRID_CUBE_SIZE_X,
    CubeGridUtils.GRID_CUBE_SIZE_Y,
    CubeGridUtils.GRID_CUBE_SIZE_Z
)