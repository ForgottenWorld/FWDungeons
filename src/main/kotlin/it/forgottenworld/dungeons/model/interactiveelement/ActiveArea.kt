package it.forgottenworld.dungeons.model.interactiveelement

import it.forgottenworld.dungeons.config.ConfigManager
import it.forgottenworld.dungeons.model.box.Box
import it.forgottenworld.dungeons.utils.ktx.toVector
import it.forgottenworld.dungeons.utils.repeatedlySpawnParticles
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.util.BlockVector
import kotlin.random.Random

data class ActiveArea(
        override val id: Int,
        override val box: Box,
        val startingMaterial: Material = Material.AIR,
        var label: String? = null
) : InteractiveElement {

    fun fillWithMaterial(material: Material) {
        box.getAllBlocks().run {
            forEach { it.setType(material, true) }
            repeatedlySpawnParticles(Particle.PORTAL, map { it.location }, 1, 500, 4)
        }
    }

    fun getRandomLocationOnFloor() = Location(
            ConfigManager.dungeonWorld,
            Random.nextInt(box.origin.x.toInt(), box.origin.x.toInt() + box.width) + 0.5,
            box.origin.y,
            Random.nextInt(box.origin.z.toInt(), box.origin.z.toInt() + box.depth) + 0.5
    )

    override fun withContainerOrigin(oldOrigin: BlockVector, newOrigin: BlockVector) = copy(
            box = box.withContainerOrigin(oldOrigin, newOrigin)
    )

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

        fun fromConfig(id: Int, config: ConfigurationSection) = ActiveArea(
                id,
                Box.fromConfig(config),
                Material.getMaterial(config.getString("startingMaterial")!!)!!,
                config.getString("label")
        )
    }
}