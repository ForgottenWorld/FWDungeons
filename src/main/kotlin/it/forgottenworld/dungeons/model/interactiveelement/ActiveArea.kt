package it.forgottenworld.dungeons.model.interactiveelement

import it.forgottenworld.dungeons.config.ConfigManager
import it.forgottenworld.dungeons.model.box.Box
import it.forgottenworld.dungeons.utils.repeatedlySpawnParticles
import it.forgottenworld.dungeons.utils.toVector
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.util.BlockVector
import kotlin.random.Random

class ActiveArea(
        override val id: Int,
        override val box: Box,
        val startingMaterial: Material = Material.AIR) : InteractiveElement {

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
                    500,
                    4
            )
        }
    }

    fun getRandomLocationOnFloor() = Location(
            ConfigManager.dungeonWorld,
            Random.nextInt(box.origin.x.toInt(), box.origin.x.toInt() + box.width) + 0.5,
            box.origin.y,
            Random.nextInt(box.origin.z.toInt(), box.origin.z.toInt() + box.depth) + 0.5
    )

    fun withContainerOrigin(oldOrigin: BlockVector, newOrigin: BlockVector) =
            ActiveArea(id,
                    box.withContainerOrigin(oldOrigin, newOrigin),
                    startingMaterial
            ).also { it.label = label }

    fun toConfig(config: ConfigurationSection) = config.run {
        set("id", id)
        label?.let { l -> set("label", l) }
        set("origin", box.origin.toVector())
        set("width", box.width)
        set("height", box.height)
        set("depth", box.depth)
        set("startingMaterial", startingMaterial.name)
    }
    
    companion object {
        fun fromConfig(id: Int, config: ConfigurationSection) =
                ActiveArea(
                        id,
                        Box.fromConfig(config),
                        Material.getMaterial(config.getString("startingMaterial")!!)!!
                ).apply { config.getString("label")?.let { label = it } }
    }
}