package it.forgottenworld.dungeons.utils

data class Vector3i(private val components: IntArray) {

    constructor(x: Int, y: Int, z: Int): this(intArrayOf(x,y,z))

    val x get() = components[0]
    val y get() = components[1]
    val z get() = components[2]

    operator fun get(index: Int) = components[index]

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        return x == (other as Vector3i).x && y == other.y && z == other.z
    }

    override fun hashCode() = 31 * (31 * x + y) + z

    companion object {
        val ZERO = Vector3i(0,0,0)
    }
}