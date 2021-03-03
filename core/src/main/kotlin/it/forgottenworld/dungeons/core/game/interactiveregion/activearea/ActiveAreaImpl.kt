package it.forgottenworld.dungeons.core.game.interactiveregion.activearea

import com.google.inject.Inject
import com.google.inject.assistedinject.Assisted
import it.forgottenworld.dungeons.api.game.dungeon.instance.DungeonInstance
import it.forgottenworld.dungeons.api.game.interactiveregion.ActiveArea
import it.forgottenworld.dungeons.api.math.Box
import it.forgottenworld.dungeons.api.math.Vector3i
import it.forgottenworld.dungeons.api.storage.Storage
import it.forgottenworld.dungeons.core.config.Configuration
import it.forgottenworld.dungeons.core.utils.ParticleSpammer
import org.bukkit.Location
import org.bukkit.Material
import kotlin.random.Random

class ActiveAreaImpl @Inject constructor(
    @Assisted override val id: Int,
    @Assisted override val box: Box,
    @Assisted override val startingMaterial: Material = Material.AIR,
    @Assisted override var label: String? = null,
    private val configuration : Configuration,
    private val activeAreaFactory: ActiveAreaFactory
) : ActiveArea, Storage.Storable {

    override fun fillWithMaterial(material: Material, instance: DungeonInstance) {
        val blocks = box.getAllBlocks(configuration.dungeonWorld, instance.origin)
        for (it in blocks) it.setType(material, true)
        ParticleSpammer.activeAreaSwirls(box, configuration.dungeonWorld)
    }

    override fun getRandomLocationOnFloor(dungeonInstance: DungeonInstance): Location {
        val origin = box.origin.withRefSystemOrigin(Vector3i.ZERO, dungeonInstance.origin)
        return Location(
            configuration.dungeonWorld,
            Random.nextInt(origin.x, origin.x + box.width) + 0.5,
            origin.y.toDouble(),
            Random.nextInt(origin.z, origin.z + box.depth) + 0.5
        )
    }

    override fun withContainerOrigin(oldOrigin: Vector3i, newOrigin: Vector3i) = activeAreaFactory.create(
        id,
        box.withContainerOrigin(oldOrigin, newOrigin),
        startingMaterial,
        label
    )

    override fun withContainerOriginZero(oldOrigin: Vector3i) = withContainerOrigin(oldOrigin, Vector3i.ZERO)
}