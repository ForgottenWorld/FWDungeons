package it.forgottenworld.dungeons.model

import it.forgottenworld.dungeons.config.ConfigManager
import it.forgottenworld.dungeons.utils.maxBlockVector
import it.forgottenworld.dungeons.utils.minBlockVector
import it.forgottenworld.dungeons.utils.repeatedlySpawnParticles
import org.bukkit.Bukkit.getWorld
import org.bukkit.Particle
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.util.BlockVector

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

    fun withContainerOrigin(oldContainerOrigin: BlockVector, newOrigin: BlockVector) = Box(
            BlockVector(
                    origin.x - oldContainerOrigin.x + newOrigin.x,
                    origin.y - oldContainerOrigin.y + newOrigin.y,
                    origin.z - oldContainerOrigin.z + newOrigin.z),
            width, height, depth)

    fun getAllBlocks(): Set<Block> {
        val blocks = mutableSetOf<Block>()
        val world = getWorld(ConfigManager.dungeonWorld)
        for (x in 0 until width) {
            for (y in 0 until height) {
                for (z in 0 until depth) {
                    blocks.add(world!!.getBlockAt(origin.blockX + x, origin.blockY + y, origin.blockZ + z))
                }
            }
        }
        return blocks
    }

    fun getFrontierBlocks() : Set<Block> {
        val blocks = mutableSetOf<Block>()
        val world = getWorld(ConfigManager.dungeonWorld)
        for (x in 0 until width) {
            for (y in 0 until height) {
                for (z in 0 until depth) {
                    var c = 0
                    if (x == 0 || x == width - 1) ++c
                    if (y == 0 || y == height - 1) ++c
                    if (z == 0 || z == depth - 1) ++c
                    if (c > 1)
                        blocks.add(world!!.getBlockAt(origin.blockX + x, origin.blockY + y, origin.blockZ + z))
                }
            }
        }
        return blocks
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