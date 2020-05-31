package it.forgottenworld.dungeons.model.dungeon

import it.forgottenworld.dungeons.model.box.Box
import org.bukkit.block.Block

class DungeonBox : Box {

    constructor(xRange : List<Int>,
                yRange : List<Int>,
                zRange : List<Int>) {
        this.xRange = xRange
        this.yRange = yRange
        this.zRange = zRange
    }

    constructor(block1: Block, block2: Block) {
        val locs = listOf(block1.location, block2.location)
        xRange = locs.map { it.blockX }.sorted()
        yRange = locs.map { it.blockY }.sorted()
        zRange = locs.map { it.blockZ }.sorted()
    }

    constructor(creationData: DungeonBoxCreationData) {
        xRange = listOf(creationData.origin.x, creationData.origin.x + creationData.width)
        yRange = listOf(creationData.origin.y, creationData.origin.x + creationData.height)
        zRange = listOf(creationData.origin.z, creationData.origin.x + creationData.depth)
    }
}