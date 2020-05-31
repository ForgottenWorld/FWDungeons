package it.forgottenworld.dungeons.model.trigger

import it.forgottenworld.dungeons.model.box.Box
import org.bukkit.block.Block

class TriggerBox : Box {

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
}