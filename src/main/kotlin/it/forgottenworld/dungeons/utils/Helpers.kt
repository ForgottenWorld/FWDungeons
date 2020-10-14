package it.forgottenworld.dungeons.utils

import org.bukkit.block.Block
import org.bukkit.util.BlockVector
import kotlin.math.max
import kotlin.math.min

fun minBlockVector(block1: Block, block2: Block) : BlockVector =
        BlockVector(
                min(block1.x, block2.x),
                min(block1.y, block2.y),
                min(block1.z, block2.z)
        )

fun maxBlockVector(block1: Block, block2: Block) : BlockVector =
        BlockVector(
                max(block1.x, block2.x),
                max(block1.y, block2.y),
                max(block1.z, block2.z)
        )