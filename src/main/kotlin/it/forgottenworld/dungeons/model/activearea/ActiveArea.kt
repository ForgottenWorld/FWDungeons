package it.forgottenworld.dungeons.model.activearea

import it.forgottenworld.dungeons.model.box.Box
import it.forgottenworld.dungeons.utils.repeatedlySpawnParticles
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Particle

class ActiveArea(val id:Int, val box:Box, val startingMaterial: Material = Material.AIR) {

    var label: String? = null

    fun fillWithMaterial(material: Material) {
        box.getAllBlocks().apply {
            forEach {
                it.setType(material, true)
            }
            repeatedlySpawnParticles(
                    Particle.PORTAL,
                    map{ it.location }.toSet(),
                    1,
                    10,
                    4
            )
        }
    }

    fun getRandomLocationOnFloor(): Location {
        val floorBlocks = box.getFloorBlocks()
        return floorBlocks.random().location.clone().add(0.0,1.0,0.0)
    }

}