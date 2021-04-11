package it.forgottenworld.dungeons.api.math

class NestableGrid3iToNi(
    width: Int,
    height: Int,
    depth: Int,
    val cellSize: Int,
    private val origin: Vector3i = Vector3i.ZERO
) {

    private val values =
        Array(width / cellSize + if (width % cellSize == 0) 0 else 1) {
            Array(height / cellSize + if (height % cellSize == 0) 0 else 1) {
                Array<IntArray?>(depth / cellSize + if (depth % cellSize == 0) 0 else 1) {
                    null
                }
            }
        }

    private val nested = mutableListOf<NestableGrid3iToNi>()

    val indices: Set<Vector3i> get() {
        val res = mutableSetOf<Vector3i>()
        for (x in values.indices) {
            for (y in values[0].indices) {
                for (z in values[0][0].indices) {
                    res.add(
                        Vector3i(
                            origin.x + x * cellSize,
                            origin.y + y * cellSize,
                            origin.z + z * cellSize
                        )
                    )
                }
            }
        }
        return res
    }

    fun nestAt(index1: Int, index2: Int, index3: Int): NestableGrid3iToNi {
        val nestedGrid = NestableGrid3iToNi(
            cellSize,
            cellSize,
            cellSize,
            cellSize / 2,
            Vector3i(index1, index2, index3)
        )
        nested.add(nestedGrid)
        val actualX = (index1 - origin.x) / cellSize
        val actualY = (index2 - origin.y) / cellSize
        val actualZ = (index3 - origin.z) / cellSize
        values[actualX][actualY][actualZ] = intArrayOf(-nested.size)
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
        return nested[-current[0] - 1][index1,index2,index3]
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
        values[actualX][actualY][actualZ] = value
    }
}