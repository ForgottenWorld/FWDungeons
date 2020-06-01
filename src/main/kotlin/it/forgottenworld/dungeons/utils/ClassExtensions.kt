package it.forgottenworld.dungeons.utils

import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.Player

fun Player.getTargetBlock() : Block? {
    return this.getTargetBlock(null as Set<Material>?, 5)
}