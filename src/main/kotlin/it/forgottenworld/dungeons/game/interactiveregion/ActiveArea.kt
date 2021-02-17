package it.forgottenworld.dungeons.game.interactiveregion

import it.forgottenworld.dungeons.config.ConfigManager
import it.forgottenworld.dungeons.game.box.Box
import it.forgottenworld.dungeons.game.instance.DungeonFinalInstance
import it.forgottenworld.dungeons.game.instance.DungeonInstance
import it.forgottenworld.dungeons.utils.ParticleSpammer.Companion.repeatedlySpawnParticles
import it.forgottenworld.dungeons.utils.Vector3i
import it.forgottenworld.dungeons.utils.toVector
import it.forgottenworld.dungeons.utils.withRefSystemOrigin
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.configuration.ConfigurationSection
import kotlin.random.Random

data class ActiveArea(
    override val id: Int,
    override val box: Box,
    val startingMaterial: Material = Material.AIR,
    var label: String? = null
) : InteractiveRegion {

    fun fillWithMaterial(material: Material, instance: DungeonFinalInstance) {
        val blocks = box.getAllBlocks(instance.origin)
        blocks.forEach { it.setType(material, true) }
        repeatedlySpawnParticles(
            Particle.PORTAL,
            blocks.map { it.location },
            1,
            500,
            4
        )
    }

    fun getRandomLocationOnFloor(dungeonInstance: DungeonInstance): Location {
        val origin = box.origin.withRefSystemOrigin(Vector3i.ZERO, dungeonInstance.origin)
        return Location(
            ConfigManager.dungeonWorld,
            Random.nextInt(origin.x, origin.x + box.width) + 0.5,
            origin.y.toDouble(),
            Random.nextInt(origin.z, origin.z + box.depth) + 0.5
        )
    }

    override fun withContainerOrigin(oldOrigin: Vector3i, newOrigin: Vector3i) = copy(
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