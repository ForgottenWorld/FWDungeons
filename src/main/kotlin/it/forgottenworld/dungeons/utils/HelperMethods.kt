package it.forgottenworld.dungeons.utils

import org.bukkit.block.Block
import org.bukkit.util.BlockVector
import java.lang.Integer.min

fun minBlockVector(block1: Block, block2: Block) : BlockVector =
        BlockVector(
                min(block1.x, block2.x),
                min(block1.y, block2.y),
                min(block1.z, block2.z)
        )