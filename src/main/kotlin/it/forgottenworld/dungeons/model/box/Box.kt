package it.forgottenworld.dungeons.model.box

import it.forgottenworld.dungeons.config.ConfigManager
import it.forgottenworld.dungeons.utils.max
import it.forgottenworld.dungeons.utils.min
import it.forgottenworld.dungeons.utils.repeatedlySpawnParticles
import org.bukkit.Particle
import org.bukkit.block.Block
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player
import org.bukkit.util.BlockVector
import org.bukkit.util.BoundingBox
import org.bukkit.util.Vector

class Box : Cloneable {
    var origin: BlockVector
    var width: Int
    var height: Int
    var depth: Int

    constructor(origin: BlockVector,
                width: Int,
                height: Int,
                depth: Int) {
        this.origin = origin
        this.width = width
        this.height = height
        this.depth = depth
    }

    constructor(pos1: BlockVector, pos2: BlockVector) {
        origin = pos1 min pos2
        val opposite = pos1 max pos2
        this.width = opposite.blockX - origin.blockX + 1
        this.height = opposite.blockY - origin.blockY + 1
        this.depth = opposite.blockZ - origin.blockZ + 1
    }

    val boundingBox
        get() = BoundingBox.of(origin, BlockVector(
            origin.x + width,
            origin.y + height,
            origin.z + depth))

    fun containsPlayer(player: Player) = player.location.let {
        it.x >= origin.blockX && it.x <= origin.blockX + width &&
            it.y >= origin.blockY && it.y <= origin.blockY + height &&
            it.z >= origin.blockZ && it.z <= origin.blockZ + depth
    }

    fun containsBlock(block: Block) = block.location.let {
        it.x >= origin.blockX && it.x < origin.blockX + width &&
            it.y >= origin.blockY && it.y < origin.blockY + height &&
            it.z >= origin.blockZ && it.z < origin.blockZ + depth
    }

    fun containsVector(vector: Vector) = vector.run {
        x >= origin.blockX && x < origin.blockX + width &&
            y >= origin.blockY && y < origin.blockY + height &&
            z >= origin.blockZ && z < origin.blockZ + depth
    }

    fun withOriginZero() = Box(BlockVector(0, 0, 0), width, height, depth)

    fun withOrigin(origin: BlockVector) = Box(origin, width, height, depth)

    fun withContainerOrigin(oldContainerOrigin: BlockVector, newOrigin: BlockVector) = Box(
        BlockVector(
            origin.x - oldContainerOrigin.x + newOrigin.x,
            origin.y - oldContainerOrigin.y + newOrigin.y,
            origin.z - oldContainerOrigin.z + newOrigin.z),
        width, height, depth)

    fun getAllBlocks(): Set<Block> {
        val blocks = mutableSetOf<Block>()
        val world = ConfigManager.dungeonWorld
        for (x in 0 until width) {
            for (y in 0 until height) {
                for (z in 0 until depth) {
                    blocks.add(world.getBlockAt(origin.blockX + x, origin.blockY + y, origin.blockZ + z))
                }
            }
        }
        return blocks
    }

    fun getFrontier(): Set<BlockVector> {
        val vecs = mutableSetOf<BlockVector>()
        for (x in 0 until width) {
            for (y in 0 until height) {
                for (z in 0 until depth) {
                    var c = 0
                    if (x == 0 || x == width - 1) ++c
                    if (y == 0 || y == height - 1) ++c
                    if (z == 0 || z == depth - 1) ++c
                    if (c > 1)
                        vecs.add(BlockVector(origin.blockX + x, origin.blockY + y, origin.blockZ + z))
                }
            }
        }
        return vecs
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

    public override fun clone() = Box(origin.clone(), width, height, depth)

    companion object {

        fun fromConfig(config: ConfigurationSection) = Box(
            config.getVector("origin")!!.toBlockVector(),
            config.getInt("width"),
            config.getInt("height"),
            config.getInt("depth")
        )
    }
}