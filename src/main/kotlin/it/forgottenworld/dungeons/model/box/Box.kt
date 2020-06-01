package it.forgottenworld.dungeons.model.box

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
        val locs = listOf(block1.location, block2.location)
        origin = BlockVector(block1.location.toVector())
        this.width = locs.map { it.blockX }.sorted().let { it[1] - it[0] + 1 }
        this.height = locs.map { it.blockY }.sorted().let { it[1] - it[0] + 1 }
        this.depth = locs.map { it.blockZ }.sorted().let { it[1] - it[0] + 1 }
    }

    fun containsPlayer(player: Player) = player.location.let {
        it.x > origin.blockX && it.x < origin.blockX + width &&
                it.y > origin.blockY && it.y < origin.blockY + height &&
                it.z > origin.blockZ && it.z < origin.blockZ + depth
    }

    fun containsBlock(block: Block) = block.location.let {
        it.x > origin.blockX && it.x < origin.blockX + width &&
                it.y > origin.blockY && it.y < origin.blockY + height &&
                it.z > origin.blockZ && it.z < origin.blockZ + depth
    }

    fun withOriginZero() : Box = Box(BlockVector(0, 0, 0), width, height, depth)

    fun withOrigin(origin: BlockVector) : Box = Box(origin, width, height, depth)
}