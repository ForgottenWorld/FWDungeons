package it.forgottenworld.dungeons.api.math

import it.forgottenworld.dungeons.api.storage.Storage
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.util.BoundingBox
import kotlin.math.abs

data class Box(
    val origin: Vector3i,
    val width: Int,
    val height: Int,
    val depth: Int,
) : Storage.Storable {

    fun getBoundingBox(origin: Vector3i = this.origin) = BoundingBox.of(
        origin.toBlockVector(),
        getOriginOpposite(origin).toBlockVector()
    )

    fun getOriginOpposite(origin: Vector3i = this.origin) = Vector3i(
        origin.x + width,
        origin.y + height,
        origin.z + depth
    )

    fun containsPlayer(
        player: Player,
        origin: Vector3i = this.origin
    ) = containsLocation(player.location, origin)

    fun containsBlock(
        block: Block,
        origin: Vector3i = this.origin
    ) = containsLocation(block.location, origin)

    private fun containsLocation(
        location: Location,
        origin: Vector3i = this.origin
    ) = location.let {
        it.x >= origin.x && it.x < origin.x + width &&
            it.y >= origin.y && it.y < origin.y + height &&
            it.z >= origin.z && it.z < origin.z + depth
    }

    fun containsXYZ(x: Int, y: Int, z: Int) =
        x >= origin.x && x < origin.x + width &&
            y >= origin.y && y < origin.y + height &&
            z >= origin.z && z < origin.z + depth

    fun intersects(other: Box): Boolean {
        val opposite = getOriginOpposite()
        val otherOpposite = other.getOriginOpposite()
        return opposite.x >= other.origin.x &&
            origin.x <= otherOpposite.x &&
            opposite.y >= other.origin.y &&
            origin.y <= otherOpposite.y &&
            opposite.z >= other.origin.z &&
            origin.z <= otherOpposite.z
    }

    fun withOriginZero() = Box(Vector3i.ZERO, width, height, depth)

    fun withOrigin(origin: Vector3i) = Box(origin, width, height, depth)

    fun withContainerOrigin(
        oldContainerOrigin: Vector3i,
        newOrigin: Vector3i
    ) = Box(
        Vector3i(
            origin.x - oldContainerOrigin.x + newOrigin.x,
            origin.y - oldContainerOrigin.y + newOrigin.y,
            origin.z - oldContainerOrigin.z + newOrigin.z
        ),
        width, height, depth
    )

    fun withContainerOriginZero(
        oldContainerOrigin: Vector3i
    ) = withContainerOrigin(
        oldContainerOrigin,
        Vector3i.ZERO
    )

    fun getCenterOfAllBlocks(
        containerOrigin: Vector3i = Vector3i.ZERO
    ): List<Vector3d> {
        val blocks = mutableListOf<Vector3d>()
        for (x in 0 until width) {
            for (y in 0 until height) {
                for (z in 0 until depth) {
                    blocks.add(
                        Vector3d(
                            containerOrigin.x + origin.x + x + 0.5,
                            containerOrigin.y + origin.y + y + 0.5,
                            containerOrigin.z + origin.z + z + 0.5
                        )
                    )
                }
            }
        }
        return blocks
    }

    fun getAllBlocks(
        world: World,
        containerOrigin: Vector3i = Vector3i.ZERO
    ) : List<Block> {
        val blocks = mutableListOf<Block>()
        for (x in 0 until width) {
            for (y in 0 until height) {
                for (z in 0 until depth) {
                    blocks.add(
                        world.getBlockAt(
                            containerOrigin.x + origin.x + x,
                            containerOrigin.y + origin.y + y,
                            containerOrigin.z + origin.z + z
                        )
                    )
                }
            }
        }
        return blocks
    }

    fun getBlockIterator(
        world: World,
        containerOrigin: Vector3i = Vector3i.ZERO
    ) = iterator {
        for (x in 0 until width) {
            for (y in 0 until height) {
                for (z in 0 until depth) {
                    yield(
                        world.getBlockAt(
                            containerOrigin.x + origin.x + x,
                            containerOrigin.y + origin.y + y,
                            containerOrigin.z + origin.z + z
                        )
                    )
                }
            }
        }
    }

    fun getFrame(origin: Vector3i = this.origin): List<Vector3d> {
        val minX = origin.x + 0.5
        val maxX = origin.x + width - 0.5
        val minY = origin.y + 0.5
        val maxY = origin.y + height - 0.5
        val minZ = origin.z + 0.5
        val maxZ = origin.z + depth - 0.5
        return mutableListOf(
            Vector3d(minX, minY, minZ),
            Vector3d(minX, minY, maxZ),
            Vector3d(minX, maxY, minZ),
            Vector3d(maxX, minY, minZ),
            Vector3d(maxX, maxY, minZ),
            Vector3d(minX, maxY, maxZ),
            Vector3d(maxX, minY, maxZ),
            Vector3d(maxX, maxY, maxZ)
        ).apply {
            for (x in 1 until width - 1) {
                add(Vector3d(minX + x, minY, minZ))
                add(Vector3d(minX + x, maxY, maxZ))
                add(Vector3d(minX + x, minY, maxZ))
                add(Vector3d(minX + x, maxY, minZ))
            }
            for (y in 1 until height - 1) {
                add(Vector3d(minX, minY + y, minZ))
                add(Vector3d(maxX, minY + y, maxZ))
                add(Vector3d(maxX, minY + y, minZ))
                add(Vector3d(minX, minY + y, maxZ))
            }
            for (z in 1 until depth - 1) {
                add(Vector3d(minX, minY, minZ + z))
                add(Vector3d(maxX, maxY, minZ + z))
                add(Vector3d(minX, maxY, minZ + z))
                add(Vector3d(maxX, minY, minZ + z))
            }
        }
    }

    class Builder {

        private var pos1: Vector3i? = null
        private var pos2: Vector3i? = null

        private val canBeBuilt
            get() = pos1 != null && pos2 != null

        fun clear() {
            pos1 = null
            pos2 = null
        }

        fun pos1(pos: Vector3i) {
            pos1 = pos
        }

        fun pos2(pos: Vector3i) {
            pos2 = pos
        }

        fun build(): Box? {
            if (!canBeBuilt) return null
            val box = between(pos1!!, pos2!!)
            clear()
            return box
        }
    }

    companion object {

        fun between(pos1: Vector3i, pos2: Vector3i): Box {
            val origin = pos1 min pos2
            val opposite = pos1 max pos2
            return Box(
                origin,
                abs(opposite.x - origin.x) + 1,
                abs(opposite.y - origin.y) + 1,
                abs(opposite.z - origin.z) + 1
            )
        }
    }
}