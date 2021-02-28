package it.forgottenworld.dungeons.core.game.interactiveregion

import it.forgottenworld.dungeons.api.math.Box
import it.forgottenworld.dungeons.core.config.Storage
import org.bukkit.Material
import org.bukkit.configuration.ConfigurationSection

class ActiveAreaImplStorageStrategy : Storage.StorageStrategy<ActiveAreaImpl> {

    override fun toConfig(obj: ActiveAreaImpl, config: ConfigurationSection) {
        with(config) {
            set("id", obj.id)
            obj.label?.let { l -> set("label", l) }
            set("origin", obj.box.origin.toVector())
            set("width", obj.box.width)
            set("height", obj.box.height)
            set("depth", obj.box.depth)
            set("startingMaterial", obj.startingMaterial.name)
        }
    }

    override fun fromConfig(config: ConfigurationSection) = ActiveAreaImpl(
        config.getInt("id"),
        Box.fromConfig(config),
        Material.getMaterial(config.getString("startingMaterial")!!)!!,
        config.getString("label")
    )
}