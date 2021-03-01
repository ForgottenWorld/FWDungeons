package it.forgottenworld.dungeons.core.game.interactiveregion

import com.google.inject.Inject
import it.forgottenworld.dungeons.api.game.interactiveregion.ActiveArea
import it.forgottenworld.dungeons.api.math.Box
import it.forgottenworld.dungeons.api.storage.Storage
import org.bukkit.Material
import org.bukkit.configuration.ConfigurationSection

class ActiveAreaImplStorageStrategy @Inject constructor(
    private val activeAreaFactory: ActiveAreaFactory
) : Storage.StorageStrategy<ActiveArea> {

    override fun toConfig(obj: ActiveArea, config: ConfigurationSection, storage: Storage) {
        config.set("id", obj.id)
        obj.label?.let { l -> config.set("label", l) }
        config.set("origin", obj.box.origin.toVector())
        config.set("width", obj.box.width)
        config.set("height", obj.box.height)
        config.set("depth", obj.box.depth)
        config.set("startingMaterial", obj.startingMaterial.name)
    }

    override fun fromConfig(config: ConfigurationSection, storage: Storage) = activeAreaFactory.create(
        config.getInt("id"),
        Box.fromConfig(config),
        Material.getMaterial(config.getString("startingMaterial")!!)!!,
        config.getString("label")
    )
}