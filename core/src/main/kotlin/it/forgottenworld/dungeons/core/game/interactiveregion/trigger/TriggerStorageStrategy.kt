package it.forgottenworld.dungeons.core.game.interactiveregion.trigger

import com.google.inject.Inject
import it.forgottenworld.dungeons.api.game.interactiveregion.Trigger
import it.forgottenworld.dungeons.api.serialization.edit
import it.forgottenworld.dungeons.api.serialization.read
import it.forgottenworld.dungeons.api.storage.Storage
import it.forgottenworld.dungeons.api.storage.Storage.Companion.load
import it.forgottenworld.dungeons.core.scripting.CodeParser
import org.bukkit.configuration.ConfigurationSection

class TriggerStorageStrategy @Inject constructor(
    private val triggerFactory: TriggerFactory,
    private val codeParser: CodeParser
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
            "effect" to obj.effectCode.joinToString("; ")
            "requiresWholeParty" to obj.requiresWholeParty
        }
    }

    override fun fromStorage(config: ConfigurationSection, storage: Storage) = config.read {
        triggerFactory.create(
            get("id")!!,
            storage.load(section("box")!!),
            codeParser.cleanupCode(get("effect")!!),
            get("requiresWholeParty")!!,
            get("label")
        )
    }
}