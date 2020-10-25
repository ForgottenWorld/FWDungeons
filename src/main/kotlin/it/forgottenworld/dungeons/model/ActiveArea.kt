package it.forgottenworld.dungeons.model

import it.forgottenworld.dungeons.config.ConfigManager
import it.forgottenworld.dungeons.utils.repeatedlySpawnParticles
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Particle
import kotlin.random.Random

class ActiveArea(val id:Int, val box: Box, val startingMaterial: Material = Material.AIR) {

    var label: String? = null

    fun fillWithMaterial(material: Material) {
        box.getAllBlocks().run {
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

    fun getRandomLocationOnFloor() = Location(
            Bukkit.getWorld(ConfigManager.dungeonWorld),
            Random.nextDouble(box.origin.x + 0.5, box.origin.x + box.width - 0.5),
            box.origin.y,
            Random.nextDouble(box.origin.z + 0.5, box.origin.z + box.depth - 0.5)
    )

}