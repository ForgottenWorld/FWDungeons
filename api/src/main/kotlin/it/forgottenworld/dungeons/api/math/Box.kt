package it.forgottenworld.dungeons.api.math

import org.bukkit.Location
import org.bukkit.World
import org.bukkit.block.Block
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player
import org.bukkit.util.BoundingBox
import kotlin.math.abs

class Box(
    val origin: Vector3i,
    val width: Int,
    val height: Int,
    val depth: Int,
) : Cloneable {

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

    fun getAllBlocks(world: World, containerOrigin: Vector3i = Vector3i.ZERO): Set<Block> {
        val blocks = mutableSetOf<Block>()
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

    fun getFrame(origin: Vector3i = this.origin): List<Vector3i> {
        val minX = origin.x
        val maxX = origin.x + width - 1
        val minY = origin.y
        val maxY = origin.y + height - 1
        val minZ = origin.z
        val maxZ = origin.z + depth - 1
        return mutableListOf(
            Vector3i(minX, minY, minZ),
            Vector3i(minX, minY, maxZ),
            Vector3i(minX, maxY, minZ),
            Vector3i(maxX, minY, minZ),
            Vector3i(maxX, maxY, minZ),
            Vector3i(minX, maxY, maxZ),
            Vector3i(maxX, minY, maxZ),
            Vector3i(maxX, maxY, maxZ)
        ).apply {
            for (x in 1 until width - 1) {
                add(Vector3i(minX + x, minY, minZ))
                add(Vector3i(minX + x, maxY, maxZ))
                add(Vector3i(minX + x, minY, maxZ))
                add(Vector3i(minX + x, maxY, minZ))
            }
            for (y in 1 until height - 1) {
                add(Vector3i(minX, minY + y, minZ))
                add(Vector3i(maxX, minY + y, maxZ))
                add(Vector3i(maxX, minY + y, minZ))
                add(Vector3i(minX, minY + y, maxZ))
            }
            for (z in 1 until depth - 1) {
                add(Vector3i(minX, minY, minZ + z))
                add(Vector3i(maxX, maxY, minZ + z))
                add(Vector3i(minX, maxY, minZ + z))
                add(Vector3i(maxX, minY, minZ + z))
            }
        }
    }

    public override fun clone() = Box(origin.copy(), width, height, depth)

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

        fun fromConfig(config: ConfigurationSection) = Box(
            Vector3i.ofBukkitVector(config.getVector("origin")!!),
            config.getInt("width"),
            config.getInt("height"),
            config.getInt("depth")
        )

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