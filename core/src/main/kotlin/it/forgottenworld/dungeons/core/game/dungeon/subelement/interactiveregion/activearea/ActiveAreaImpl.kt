package it.forgottenworld.dungeons.core.game.dungeon.subelement.interactiveregion.activearea

import com.google.inject.Inject
import com.google.inject.assistedinject.Assisted
import it.forgottenworld.dungeons.api.game.dungeon.instance.DungeonInstance
import it.forgottenworld.dungeons.api.game.dungeon.subelement.interactiveregion.ActiveArea
import it.forgottenworld.dungeons.api.math.Box
import it.forgottenworld.dungeons.api.storage.Storage
import it.forgottenworld.dungeons.core.storage.Configuration
import it.forgottenworld.dungeons.core.utils.ParticleSpammer
import org.bukkit.Material
import org.bukkit.Particle
import javax.annotation.Nullable

class ActiveAreaImpl @Inject constructor(
    @Assisted override val id: Int,
    @Assisted override val box: Box,
    @Assisted override val startingMaterial: Material = Material.AIR,
    @Nullable @Assisted override var label: String? = null,
    private val configuration : Configuration
) : ActiveArea, Storage.Storable {

    override fun fillWithMaterial(material: Material, instance: DungeonInstance) {
        val blocks = box.getAllBlocks(configuration.dungeonWorld, instance.origin)
        for (it in blocks) it.setType(material, true)
        ParticleSpammer.builder()
            .particle(Particle.PORTAL)
            .locations(box.getCenterOfAllBlocks())
            .world(configuration.dungeonWorld)
            .oneShot(4)
    }
}