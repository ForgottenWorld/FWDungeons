package it.forgottenworld.dungeons.model.box

import org.bukkit.block.Block
import org.bukkit.entity.Player

abstract class Box {
    lateinit var xRange : List<Int>
    lateinit var yRange : List<Int>
    lateinit var zRange : List<Int>

    val width : Int
        get() = xRange[1] - xRange[0]

    val height : Int
        get() = yRange[1] - yRange[0]

    val depth : Int
        get() = zRange[1] - zRange[0]

    fun containsPlayer(player: Player) = player.location.let {
        it.x > xRange[0] && it.x < xRange[1] &&
                it.y > yRange[0] && it.y < yRange[1] &&
                it.z > zRange[0] && it.z < zRange[1]
    }

}