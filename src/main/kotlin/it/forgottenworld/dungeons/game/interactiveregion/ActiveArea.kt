package it.forgottenworld.dungeons.game.interactiveregion

import it.forgottenworld.dungeons.config.ConfigManager
import it.forgottenworld.dungeons.game.box.Box
import it.forgottenworld.dungeons.game.dungeon.FinalDungeon
import it.forgottenworld.dungeons.game.instance.DungeonFinalInstance
import it.forgottenworld.dungeons.utils.ParticleSpammer.Companion.repeatedlySpawnParticles
import it.forgottenworld.dungeons.utils.toVector
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.util.BlockVector
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

    class FinalInstanceActiveAreaDelegate private constructor(
        dungeon: FinalDungeon,
        newOrigin: BlockVector
    ) {

        private val activeAreas = dungeon
            .activeAreas
            .entries
            .associate { (k, v) -> k to v.withContainerOrigin(BlockVector(0, 0, 0), newOrigin) }

        operator fun getValue(thisRef: Any?, property: KProperty<*>) = activeAreas

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