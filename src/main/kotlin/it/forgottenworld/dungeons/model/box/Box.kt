package it.forgottenworld.dungeons.model.box

import it.forgottenworld.dungeons.config.ConfigManager
import it.forgottenworld.dungeons.model.trigger.Trigger
import it.forgottenworld.dungeons.utils.maxBlockVector
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
        origin = minBlockVector(block1, block2)
        val opposite = maxBlockVector(block1, block2)
        this.width = opposite.blockX - origin.blockX + 1
        this.height = opposite.blockY - origin.blockY + 1
        this.depth = opposite.blockZ - origin.blockZ + 1
    }

    constructor(blockVector1: BlockVector, blockVector2: BlockVector) {
        origin = minBlockVector(blockVector1, blockVector2)
        val opposite = maxBlockVector(blockVector1, blockVector2)
        this.width = opposite.blockX - origin.blockX + 1
        this.height = opposite.blockY - origin.blockY + 1
        this.depth = opposite.blockZ - origin.blockZ + 1
    }

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

    fun withOriginZero() : Box = Box(BlockVector(0, 0, 0), width, height, depth)

    fun withOrigin(origin: BlockVector) : Box = Box(origin, width, height, depth)

    fun randomLocationOnFloor() =
        Location(getWorld(ConfigManager.dungeonWorld),
                Random.nextDouble(origin.x, origin.x + width),
                origin.y,
                Random.nextDouble(origin.z, origin.z + depth))

    fun withContainerOrigin(oldContainerOrigin: BlockVector, newOrigin: BlockVector) =
            Box(BlockVector(
                    origin.x - oldContainerOrigin.x + newOrigin.x,
                    origin.y - oldContainerOrigin.y + newOrigin.y,
                    origin.z - oldContainerOrigin.z + newOrigin.z), width, height, depth)

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
        val locs = mutableSetOf<Location>()

        for (x in 0..width) {
            for (y in 0..height) {
                for (z in 0..depth) {
                    var c = 0
                    if (x == origin.blockX || x == origin.blockX + width - 1) ++c
                    if (y == origin.blockY || y == origin.blockY + height - 1) ++c
                    if (z == origin.blockZ || z == origin.blockZ + depth - 1) ++c
                    if (c > 1)
                        locs.add(
                                Location(world, origin.x + x.toDouble(), origin.y + y.toDouble(), origin.z + z.toDouble()))
                }
            }
        }

        repeatedlySpawnParticles(
                Particle.COMPOSTER,
                locs,
                100,
                10,
                20
        )
    }

    fun highlightAll() {
        repeatedlySpawnParticles(
                Particle.COMPOSTER,
                getAllBlocks().map{ it.location }.toSet(),
                1,
                10,
                20
        )
    }
}