package it.forgottenworld.dungeons.api.math

import org.bukkit.Location
import org.bukkit.World
import org.bukkit.util.BlockVector
import org.bukkit.util.Vector
import kotlin.math.max
import kotlin.math.min

fun Vector3i.locationInWorld(world: World) = Location(world, x.toDouble(), y.toDouble(), z.toDouble())

fun Vector3i.toVector() = Vector(x, y, z)

fun Vector.toVector3i() = Vector3i(blockX, blockY, blockZ)

fun Vector3i.toBlockVector() = BlockVector(x, y, z)

infix fun Vector3i.min(other: Vector3i) = Vector3i(
    min(x, other.x),
    min(y, other.y),
    min(z, other.z)
)

infix fun Vector3i.max(other: Vector3i) = Vector3i(
    max(x, other.x),
    max(y, other.y),
    max(z, other.z)
)

fun World.getBlockAt(vector3i: Vector3i) = getBlockAt(
    vector3i.x,
    vector3i.y,
    vector3i.z
)

fun Vector3i.withRefSystemOrigin(
    oldOrigin: Vector3i,
    newOrigin: Vector3i
) = Vector3i(
    x - oldOrigin.x + newOrigin.x,
    y - oldOrigin.y + newOrigin.y,
    z - oldOrigin.z + newOrigin.z
)