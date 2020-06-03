package it.forgottenworld.dungeons.utils

import org.bukkit.block.Block
import org.bukkit.util.BlockVector
import java.lang.Integer.max
import java.lang.Integer.min

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

fun minBlockVector(blockVector1: BlockVector, blockVector2: BlockVector) : BlockVector =
        BlockVector(
                min(blockVector1.x.toInt(), blockVector2.x.toInt()),
                min(blockVector1.y.toInt(), blockVector2.y.toInt()),
                min(blockVector1.z.toInt(), blockVector2.z.toInt())
        )

fun maxBlockVector(blockVector1: BlockVector, blockVector2: BlockVector) : BlockVector =
        BlockVector(
                max(blockVector1.x.toInt(), blockVector2.x.toInt()),
                max(blockVector1.y.toInt(), blockVector2.y.toInt()),
                max(blockVector1.z.toInt(), blockVector2.z.toInt())
        )