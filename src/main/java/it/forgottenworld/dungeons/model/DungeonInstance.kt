package it.forgottenworld.dungeons.model

import org.bukkit.Location
import org.bukkit.block.Block
import org.bukkit.entity.Player

class DungeonInstance {
    var id = -1
    lateinit var dungeon : Dungeon
    lateinit var box : DungeonBox
}