package it.forgottenworld.dungeons.utils

import kotlin.math.ceil

class NestableGrid3iToNiPos(
    sizeX: Int,
    sizeY: Int,
    sizeZ: Int,
    private val cellSize: Int,
    private val origin: Vector3i = Vector3i(0,0,0)
) {
    init {
        if (sizeX < cellSize || sizeY < cellSize || sizeZ < this.cellSize) {
            throw IllegalArgumentException("Cell size can't be greater than grid size")
        }
    }

    private val values = Array(ceil(sizeX.toDouble() / cellSize).toInt()) {
        Array(ceil(sizeY.toDouble() / cellSize).toInt()) {
            Array<IntArray?>(ceil(sizeZ.toDouble() / this.cellSize).toInt()) { null }
        }
    }

    private val nested = mutableListOf<NestableGrid3iToNiPos>()

    val indices: Set<Vector3i> get() {
        val res = mutableSetOf<Vector3i>()
        for (x in values.indices) {
            for (y in values[0].indices) {
                for (z in values[0][0].indices) {
                    res.add(Vector3i(x * cellSize, y * cellSize, z * cellSize))
                }
            }
        }
        return res
    }

    fun nestAt(index1: Int, index2: Int, index3: Int): NestableGrid3iToNiPos {
        val nestedGrid = NestableGrid3iToNiPos(
            cellSize,
            cellSize,
            cellSize,
            cellSize / 2,
            Vector3i(
                index1,
                index2,
                index3
            )
        )
        nested.add(nestedGrid)
        values[index1][index2][index3] = intArrayOf(-nested.size)
        return nestedGrid
    }

    operator fun get(
        index1: Int,
        index2: Int,
        index3: Int
    ): IntArray? {
        val actualX = (index1 - origin.x) / cellSize
        val actualY = (index2 - origin.y) / cellSize
        val actualZ = (index3 - origin.z) / cellSize
        val current = values[actualX][actualY][actualZ]
        if (current == null || current[0] >= 0) return current
        return nested[-current[0]][actualX,actualY,actualZ]
    }

    operator fun set(
        index1: Int,
        index2: Int,
        index3: Int,
        value: IntArray
    ) {
        val actualX = (index1 - origin.x) / cellSize
        val actualY = (index2 - origin.y) / cellSize
        val actualZ = (index3 - origin.z) / cellSize
        val current = values[actualX][actualY][actualZ]
        if (current == null || current[0] >= 0){
            values[actualX][actualY][actualZ] = value
        } else {
            nested[-current[0]][actualX,actualY,actualZ] = value
        }
    }
}