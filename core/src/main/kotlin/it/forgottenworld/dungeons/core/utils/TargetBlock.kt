package it.forgottenworld.dungeons.core.utils

import org.bukkit.Material
import org.bukkit.entity.Player

fun Player.getTargetSolidBlock(maxDistance: Int = 5) = getTargetBlock(
    null as Set<Material>?,
    maxDistance
)