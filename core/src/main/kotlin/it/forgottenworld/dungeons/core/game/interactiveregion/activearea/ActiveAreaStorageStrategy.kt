package it.forgottenworld.dungeons.core.game.interactiveregion.activearea

import com.google.inject.Inject
import it.forgottenworld.dungeons.api.game.interactiveregion.ActiveArea
import it.forgottenworld.dungeons.api.serialization.edit
import it.forgottenworld.dungeons.api.serialization.read
import it.forgottenworld.dungeons.api.storage.Storage
import it.forgottenworld.dungeons.api.storage.Storage.Companion.load
import org.bukkit.Material
import org.bukkit.configuration.ConfigurationSection

class ActiveAreaStorageStrategy @Inject constructor(
    private val activeAreaFactory: ActiveAreaFactory
) : Storage.StorageStrategy<ActiveArea> {

    override fun toStorage(
        obj: ActiveArea,
        config: ConfigurationSection,
        storage: Storage
    ) {
        config.edit { 
            "id" to obj.id
            obj.label?.let { l -> "label" to l }
            storage.save(obj.box, section("box"))
            "startingMaterial" to obj.startingMaterial.name
        }
    }

    override fun fromStorage(config: ConfigurationSection, storage: Storage) = config.read {
        activeAreaFactory.create(
            get("id")!!,
            storage.load(section("box")!!),
            Material.getMaterial(get("startingMaterial")!!)!!,
            get("label")
        )
    }
}