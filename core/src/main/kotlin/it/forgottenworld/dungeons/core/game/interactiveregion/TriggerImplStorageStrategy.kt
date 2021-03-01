package it.forgottenworld.dungeons.core.game.interactiveregion

import com.google.inject.Inject
import it.forgottenworld.dungeons.api.game.interactiveregion.Trigger
import it.forgottenworld.dungeons.api.math.Box
import it.forgottenworld.dungeons.api.storage.Storage
import it.forgottenworld.dungeons.core.scripting.CodeParser
import org.bukkit.configuration.ConfigurationSection

class TriggerImplStorageStrategy @Inject constructor(
    private val triggerFactory: TriggerFactory
) : Storage.StorageStrategy<Trigger> {

    override fun toConfig(obj: Trigger, config: ConfigurationSection, storage: Storage) {
        config.set("id", obj.id)
        obj.label?.let { config.set("label", it) }
        config.set("origin", obj.origin.toVector())
        config.set("width", obj.box.width)
        config.set("height", obj.box.height)
        config.set("depth", obj.box.depth)
        config.set("effect", obj.effectCode.joinToString("; "))
        config.set("requiresWholeParty", obj.requiresWholeParty)
    }

    override fun fromConfig(config: ConfigurationSection, storage: Storage) = triggerFactory.create(
        config.getInt("id"),
        Box.fromConfig(config),
        CodeParser.cleanupCode(config.getString("effect")!!),
        config.getBoolean("requiresWholeParty"),
        config.getString("label")
    )
}