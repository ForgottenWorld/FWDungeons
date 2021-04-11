package it.forgottenworld.dungeons.core.game.dungeon.subelement.interactiveregion.activearea

import com.google.inject.Inject
import it.forgottenworld.dungeons.api.game.dungeon.subelement.interactiveregion.ActiveArea
import it.forgottenworld.dungeons.api.storage.Storage
import it.forgottenworld.dungeons.api.storage.Storage.Companion.load
import it.forgottenworld.dungeons.api.storage.Storage.Companion.save
import it.forgottenworld.dungeons.api.storage.edit
import it.forgottenworld.dungeons.api.storage.read
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