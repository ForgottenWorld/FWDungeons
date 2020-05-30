package it.forgottenworld.dungeons.model

import org.bukkit.block.Block
import org.bukkit.entity.Player

class DungeonBox(var xRange : List<Double>, var yRange : List<Double>, var zRange : List<Double>) {

    constructor(block1: Block, block2: Block) {
        val locs = listOf(block1.location, block2.location)
        xRange = locs.map { it.x }.sorted()
        yRange = locs.map { it.y }.sorted()
        zRange = locs.map { it.z }.sorted()
    }

    val width : Double
        get() = xRange[1] - xRange[0]

    val height : Double
        get() = yRange[1] - yRange[0]

    val depth : Double
        get() = zRange[1] - zRange[0]

    fun containsPlayer(player: Player) = player.location.let {
        it.x > xRange[0] && it.x < xRange[1] &&
                it.y > yRange[0] && it.y < yRange[1] &&
                it.z > zRange[0] && it.z < zRange[1]
    }

}