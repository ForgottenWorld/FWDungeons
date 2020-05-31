package it.forgottenworld.dungeons.model.dungeon

import org.bukkit.block.Block

data class DungeonBoxCreationData (
        val origin: Block,
        var width: Int,
        var height: Int,
        var depth: Int
)