package it.forgottenworld.dungeons.game.box

import it.forgottenworld.dungeons.config.ConfigManager
import it.forgottenworld.dungeons.utils.ParticleSpammer.Companion.repeatedlySpawnParticles
import it.forgottenworld.dungeons.utils.Vector3i
import it.forgottenworld.dungeons.utils.max
import it.forgottenworld.dungeons.utils.min
import it.forgottenworld.dungeons.utils.toBlockVector
import it.forgottenworld.dungeons.utils.toVector3i
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.block.Block
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player
import org.bukkit.util.BoundingBox

class Box(
    val origin: Vector3i,
    val width: Int,
    val height: Int,
    val depth: Int,
) : Cloneable {

    val boundingBox
        get() = BoundingBox.of(
            origin.toBlockVector(),
            originOpposite.toBlockVector()
        )

    val originOpposite get() = Vector3i(
        origin.x + width,
        origin.y + height,
        origin.z + depth
    )

    fun containsPlayer(player: Player) = containsLocation(player.location)

    fun containsBlock(block: Block) = containsLocation(block.location)

    private fun containsLocation(location: Location) = location.let {
        it.x >= origin.x && it.x < origin.x + width &&
            it.y >= origin.y && it.y < origin.y + height &&
            it.z >= origin.z && it.z < origin.z + depth
    }

    fun containsXYZ(x: Int, y: Int, z: Int) =
        x >= origin.x && x < origin.x + width &&
            y >= origin.y && y < origin.y + height &&
            z >= origin.z && z < origin.z + depth

    fun containsVector(vector: Vector3i) = vector.run {
        x >= origin.x && x < origin.x + width &&
            y >= origin.y && y < origin.y + height &&
            z >= origin.z && z < origin.z + depth
    }

    fun intersects(other: Box): Boolean {
        val opposite = originOpposite
        val otherOpposite = other.originOpposite
        return opposite.x >= other.origin.x &&
            origin.x <= otherOpposite.x &&
            opposite.y >= other.origin.y &&
            origin.y <= otherOpposite.y &&
            opposite.z >= other.origin.z &&
            origin.z <= otherOpposite.z
    }

    fun withOriginZero() = Box(Vector3i(0, 0, 0), width, height, depth)

    fun withOrigin(origin: Vector3i) = Box(origin, width, height, depth)

    fun withContainerOrigin(oldContainerOrigin: Vector3i, newOrigin: Vector3i) = Box(
        Vector3i(
            origin.x - oldContainerOrigin.x + newOrigin.x,
            origin.y - oldContainerOrigin.y + newOrigin.y,
            origin.z - oldContainerOrigin.z + newOrigin.z
        ),
        width, height, depth
    )

    fun getAllBlocks(): Set<Block> {
        val blocks = mutableSetOf<Block>()
        val world = ConfigManager.dungeonWorld
        for (x in 0 until width) {
            for (y in 0 until height) {
                for (z in 0 until depth) {
                    blocks.add(
                        world.getBlockAt(
                            origin.x + x,
                            origin.y + y,
                            origin.z + z
                        )
                    )
                }
            }
        }
        return blocks
    }

    fun getFrame(): List<Vector3i> {
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

    fun highlightAll() {
        repeatedlySpawnParticles(
            Particle.COMPOSTER,
            getAllBlocks().map { it.location },
            1,
            500,
            20
        )
    }

    public override fun clone() = Box(origin.copy(), width, height, depth)

    fun split8(): Array<Box> {
        if (width % 2 != 0 || height % 2 != 0 || depth % 2 != 0) return arrayOf()
        val or1 = Vector3i(origin.x + width / 2, origin.y, origin.z)
        val or2 = Vector3i(origin.x, origin.y + height / 2, origin.z)
        val or3 = Vector3i(origin.x, origin.y, origin.z + depth / 2)
        val or4 = Vector3i(origin.x + width / 2, origin.y + height / 2, origin.z)
        val or5 = Vector3i(origin.x, origin.y + height / 2, origin.z + depth / 2)
        val or6 = Vector3i(origin.x + width / 2, origin.y, origin.z + depth / 2)
        val or7 = Vector3i(origin.x + width / 2, origin.y + height / 2, origin.z + depth / 2)
        return arrayOf(
            Box(origin.copy(), width / 2, height / 2, depth / 2),
            Box(or1, width / 2, height / 2, depth / 2),
            Box(or2, width / 2, height / 2, depth / 2),
            Box(or3, width / 2, height / 2, depth / 2),
            Box(or4, width / 2, height / 2, depth / 2),
            Box(or5, width / 2, height / 2, depth / 2),
            Box(or6, width / 2, height / 2, depth / 2),
            Box(or7, width / 2, height / 2, depth / 2)
        )
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

        fun fromConfig(config: ConfigurationSection) = Box(
            config.getVector("origin")!!.toVector3i(),
            config.getInt("width"),
            config.getInt("height"),
            config.getInt("depth")
        )

        fun between(pos1: Vector3i, pos2: Vector3i): Box {
            val origin = pos1 min pos2
            val opposite = pos1 max pos2
            return Box(
                origin,
                opposite.x - origin.x + 1,
                opposite.y - origin.y + 1,
                opposite.z - origin.z + 1
            )
        }
    }
}