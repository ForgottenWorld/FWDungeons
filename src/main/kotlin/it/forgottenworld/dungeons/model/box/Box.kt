package it.forgottenworld.dungeons.model.box

import it.forgottenworld.dungeons.config.ConfigManager
import it.forgottenworld.dungeons.utils.repeatedlySpawnParticles
import org.bukkit.Bukkit.getWorld
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.util.BlockIterator
import org.bukkit.util.BlockVector
import org.bukkit.util.Vector

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
        this.origin = BlockVector()
        this.width = locs.map { it.blockX }.sorted().let {
            this.origin.x = it[0].toDouble()
            it[1] - it[0] + 1
        }
        this.height = locs.map { it.blockY }.sorted().let {
            this.origin.y = it[0].toDouble()
            it[1] - it[0] + 1
        }
        this.depth = locs.map { it.blockZ }.sorted().let {
            this.origin.z = it[0].toDouble()
            it[1] - it[0] + 1
        }
        origin = BlockVector(block1.location.toVector())
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

    fun highlightFrame() {
        val world = getWorld(ConfigManager.dungeonWorld) ?: return

        for (x in 0 until width) {
            for (y in 0 until height) {
                for (z in 0 until depth) {
                    var c = 0
                    if (x == origin.blockX || x == origin.blockX + width - 1) ++c
                    if (y == origin.blockY || y == origin.blockY + height - 1) ++c
                    if (z == origin.blockZ || z == origin.blockZ + depth - 1) ++c
                    if (c > 1)
                        repeatedlySpawnParticles(
                                Particle.COMPOSTER,
                                Location(world, x.toDouble(), y.toDouble(), z.toDouble()),
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

        for (x in 0 until width) {
            for (y in 0 until height) {
                for (z in 0 until depth) {
                    repeatedlySpawnParticles(
                            Particle.COMPOSTER,
                            Location(world, x.toDouble(), y.toDouble(), z.toDouble()),
                            100,
                            10,
                            20
                    )
                }
            }
        }
    }
}