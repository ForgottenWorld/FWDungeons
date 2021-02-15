package it.forgottenworld.dungeons.game.interactiveregion

import it.forgottenworld.dungeons.config.ConfigManager
import it.forgottenworld.dungeons.game.box.Box
import it.forgottenworld.dungeons.game.dungeon.FinalDungeon
import it.forgottenworld.dungeons.game.instance.DungeonFinalInstance
import it.forgottenworld.dungeons.utils.ParticleSpammer.Companion.repeatedlySpawnParticles
import it.forgottenworld.dungeons.utils.Vector3i
import it.forgottenworld.dungeons.utils.toVector
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.configuration.ConfigurationSection
import kotlin.random.Random
import kotlin.reflect.KProperty

data class ActiveArea(
    override val id: Int,
    override val box: Box,
    val startingMaterial: Material = Material.AIR,
    var label: String? = null
) : InteractiveRegion {

    fun fillWithMaterial(material: Material) {
        box.getAllBlocks().run {
            forEach { it.setType(material, true) }
            repeatedlySpawnParticles(Particle.PORTAL, map { it.location }, 1, 500, 4)
        }
    }

    fun getRandomLocationOnFloor() = Location(
        ConfigManager.dungeonWorld,
        Random.nextInt(box.origin.x, box.origin.x + box.width) + 0.5,
        box.origin.y.toDouble(),
        Random.nextInt(box.origin.z, box.origin.z + box.depth) + 0.5
    )

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

    class FinalInstanceActiveAreaDelegate private constructor(
        dungeon: FinalDungeon,
        newOrigin: Vector3i
    ) {

        private val activeAreas = dungeon
            .activeAreas
            .entries
            .associate { (k, v) -> k to v.withContainerOrigin(Vector3i(0, 0, 0), newOrigin) }

        operator fun getValue(thisRef: DungeonFinalInstance, property: KProperty<*>) = activeAreas

        companion object {
            fun DungeonFinalInstance.instanceActiveAreas() = FinalInstanceActiveAreaDelegate(dungeon, origin)
        }
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