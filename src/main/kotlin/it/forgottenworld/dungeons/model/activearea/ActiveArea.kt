package it.forgottenworld.dungeons.model.activearea

import it.forgottenworld.dungeons.model.box.Box
import org.bukkit.Location
import org.bukkit.Material

class ActiveArea(val id:Int, val box:Box, val startingMaterial: Material = Material.AIR) {

    var label: String? = null

    fun fillWithMaterial(material: Material) {
        box.getAllBlocks().forEach {
            it.setType(material, true)
        }
    }

    fun getRandomLocationOnFloor(): Location {
        val floorBlocks = box.getFloorBlocks()
        return floorBlocks.random().location.clone().add(0.0,1.0,0.0)
    }

}