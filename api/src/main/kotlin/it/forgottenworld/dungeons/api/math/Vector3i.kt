package it.forgottenworld.dungeons.api.math

import it.forgottenworld.dungeons.api.storage.Storage
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.block.Block
import org.bukkit.util.BlockVector
import kotlin.math.max
import kotlin.math.min

data class Vector3i(
    val x: Int,
    val y: Int,
    val z: Int
) : Storage.Storable {

    fun locationInWorld(world: World?) = Location(
        world,
        x.toDouble(),
        y.toDouble(),
        z.toDouble()
    )

    fun blockInWorld(world: World) = world.getBlockAt(x,y,z)

    fun toBlockVector() = BlockVector(x, y, z)

    fun cubeWithSide(side: Int) = Box(
        this,
        side,
        side,
        side
    )

    infix fun min(other: Vector3i) = Vector3i(
        min(x, other.x),
        min(y, other.y),
        min(z, other.z)
    )

    infix fun max(other: Vector3i) = Vector3i(
        max(x, other.x),
        max(y, other.y),
        max(z, other.z)
    )

    operator fun plus(other: Vector3i) = Vector3i(
        x + other.x,
        y + other.y,
        z + other.z
    )

    fun translatePlane(
        oldOrigin: Vector3i,
        newOrigin: Vector3i
    ) = Vector3i(
        x - oldOrigin.x + newOrigin.x,
        y - oldOrigin.y + newOrigin.y,
        z - oldOrigin.z + newOrigin.z
    )

    companion object {
        val ZERO = Vector3i(0,0,0)

        fun ofLocation(location: Location) = Vector3i(
            location.blockX,
            location.blockY,
            location.blockZ
        )

        fun ofBlock(block: Block) = ofLocation(block.location)
    }
}