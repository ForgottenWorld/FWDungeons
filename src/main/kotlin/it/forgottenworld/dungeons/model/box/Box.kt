package it.forgottenworld.dungeons.model.box

import it.forgottenworld.dungeons.config.ConfigManager
import it.forgottenworld.dungeons.model.trigger.Trigger
import it.forgottenworld.dungeons.utils.minBlockVector
import it.forgottenworld.dungeons.utils.repeatedlySpawnParticles
import org.bukkit.Bukkit.getWorld
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.util.BlockIterator
import org.bukkit.util.BlockVector
import org.bukkit.util.Vector
import kotlin.random.Random

class Box {
    var origin : BlockVector
    var width : Int
    var height : Int
    var depth : Int

    constructor(origin: BlockVector,
                width : Int,
                height : Int,
                depth : Int) {
        this.origin = origin
        this.width = width
        this.height = height
        this.depth = depth
    }

    constructor(block1: Block, block2: Block) {
        val locs = listOf(block1.location, block2.location)
        this.origin = minBlockVector(block1, block2)
        this.width = locs.map { it.blockX }.sorted().let {
            it[1] - it[0] + 1
        }
        this.height = locs.map { it.blockY }.sorted().let {
            it[1] - it[0] + 1
        }
        this.depth = locs.map { it.blockZ }.sorted().let {
            it[1] - it[0] + 1
        }
    }

    constructor(blockVector1: BlockVector, blockVector2: BlockVector) {
        val locs = listOf(blockVector1, blockVector2)
        this.origin = minBlockVector(blockVector1, blockVector2)
        this.width = locs.map { it.blockX }.sorted().let {
            it[1] - it[0] + 1
        }
        this.height = locs.map { it.blockY }.sorted().let {
            it[1] - it[0] + 1
        }
        this.depth = locs.map { it.blockZ }.sorted().let {
            it[1] - it[0] + 1
        }
    }

    fun containsPlayer(player: Player) = player.location.let {
        it.x >= origin.blockX && it.x <= origin.blockX + width &&
                it.y >= origin.blockY && it.y <= origin.blockY + height &&
                it.z >= origin.blockZ && it.z <= origin.blockZ + depth
    }

    fun containsBlock(block: Block) = block.location.let {
        it.x >= origin.blockX && it.x <= origin.blockX + width &&
                it.y >= origin.blockY && it.y <= origin.blockY + height &&
                it.z >= origin.blockZ && it.z <= origin.blockZ + depth
    }

    fun withOriginZero() : Box = Box(BlockVector(0, 0, 0), width, height, depth)

    fun withOrigin(origin: BlockVector) : Box = Box(origin, width, height, depth)

    fun randomLocationOnFloor() =
        Location(getWorld(ConfigManager.dungeonWorld),
                Random.nextDouble(origin.x, origin.x + width),
                origin.y,
                Random.nextDouble(origin.z, origin.z + depth))

    fun withContainerOrigin(oldContainerOrigin: BlockVector, newOrigin: BlockVector) =
            Box(this.origin.clone().subtract(oldContainerOrigin).add(newOrigin).toBlockVector(), width, height, depth)

    fun getAllBlocks(): Set<Block> {
        val blocks = mutableSetOf<Block>()
        val world = getWorld(ConfigManager.dungeonWorld)
        for (x in 0..width) {
            for (y in 0..height) {
                for (z in 0..depth) {
                    blocks.add(world!!.getBlockAt(origin.blockX + x, origin.blockY + y, origin.blockZ + z))
                }
            }
        }
        return blocks
    }

    fun highlightFrame() {
        val world = getWorld(ConfigManager.dungeonWorld) ?: return

        for (x in 0..width) {
            for (y in 0..height) {
                for (z in 0..depth) {
                    var c = 0
                    if (x == origin.blockX || x == origin.blockX + width - 1) ++c
                    if (y == origin.blockY || y == origin.blockY + height - 1) ++c
                    if (z == origin.blockZ || z == origin.blockZ + depth - 1) ++c
                    if (c > 1)
                        repeatedlySpawnParticles(
                                Particle.COMPOSTER,
                                Location(world, origin.x + x.toDouble(), origin.y + y.toDouble(), origin.z + z.toDouble()),
                                100,
                                10,
                                20
                        )
                }
            }
        }
    }

    fun highlightAll() {
        val world = getWorld(ConfigManager.dungeonWorld) ?: return

        for (x in 0..width) {
            for (y in 0..height) {
                for (z in 0..depth) {
                    repeatedlySpawnParticles(
                            Particle.COMPOSTER,
                            Location(world, origin.x + x.toDouble(), origin.y + y.toDouble(), origin.z + z.toDouble()),
                            100,
                            20,
                            10
                    )
                }
            }
        }
    }
}