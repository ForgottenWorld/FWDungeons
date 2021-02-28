package it.forgottenworld.dungeons.core.game.interactiveregion

import it.forgottenworld.dungeons.api.math.Box
import it.forgottenworld.dungeons.core.config.Storage
import it.forgottenworld.dungeons.core.scripting.CodeParser
import org.bukkit.configuration.ConfigurationSection

class TriggerImplStorageStrategy : Storage.StorageStrategy<TriggerImpl> {

    override fun toConfig(obj: TriggerImpl, config: ConfigurationSection) {
        config.run {
            set("id", obj.id)
            obj.label?.let { set("label", it) }
            set("origin", obj.origin.toVector())
            set("width", obj.box.width)
            set("height", obj.box.height)
            set("depth", obj.box.depth)
            set("effect", obj.effectCode.joinToString("; "))
            set("requiresWholeParty", obj.requiresWholeParty)
        }
    }

    override fun fromConfig(config: ConfigurationSection) = TriggerImpl(
        config.getInt("id"),
        Box.fromConfig(config),
        CodeParser.cleanupCode(config.getString("effect")!!),
        config.getBoolean("requiresWholeParty"),
        config.getString("label")
    )
}