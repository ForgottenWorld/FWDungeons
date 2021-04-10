package it.forgottenworld.dungeons.core.game.dungeon.subelement.interactiveregion.trigger

import com.google.inject.Inject
import it.forgottenworld.dungeons.api.game.dungeon.subelement.interactiveregion.Trigger
import it.forgottenworld.dungeons.api.storage.Storage
import it.forgottenworld.dungeons.api.storage.Storage.Companion.load
import it.forgottenworld.dungeons.api.storage.Storage.Companion.save
import it.forgottenworld.dungeons.api.storage.edit
import it.forgottenworld.dungeons.api.storage.read
import org.bukkit.configuration.ConfigurationSection

class TriggerStorageStrategy @Inject constructor(
    private val triggerFactory: TriggerFactory
) : Storage.StorageStrategy<Trigger> {

    override fun toStorage(
        obj: Trigger,
        config: ConfigurationSection,
        storage: Storage
    ) {
        config.edit {
            "id" to obj.id
            obj.label?.let { "label" to it }
            storage.save(obj.box, section("box"))
            "requiresWholeParty" to obj.requiresWholeParty
        }
    }

    override fun fromStorage(config: ConfigurationSection, storage: Storage) = config.read {
        triggerFactory.create(
            get("id")!!,
            storage.load(section("box")!!),
            get("requiresWholeParty")!!,
            get("label")
        )
    }
}