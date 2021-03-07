package it.forgottenworld.dungeons.core.game.unlockables

import com.google.inject.Inject
import it.forgottenworld.dungeons.api.game.unlockables.Unlockable
import it.forgottenworld.dungeons.api.serialization.edit
import it.forgottenworld.dungeons.api.serialization.read
import it.forgottenworld.dungeons.api.storage.Storage
import org.bukkit.Material
import org.bukkit.configuration.ConfigurationSection

class UnlockableStorageStrategy @Inject constructor(
    private val unlockableFactory: UnlockableFactory
) : Storage.StorageStrategy<Unlockable> {

    override fun toStorage(
        obj: Unlockable,
        config: ConfigurationSection,
        storage: Storage
    ) {
        config.edit {
            "seriesId" to obj.seriesId
            "order" to obj.order
            "message" to obj.message
            "unlockedMessage" to obj.unlockedMessage
            section("requirements") {
                for (req in obj.requirements) {
                    when (req) {
                        is Unlockable.Requirement.Economy -> {
                            "CURRENCY" to req.amount
                        }
                        is Unlockable.Requirement.Item -> {
                            req.material.toString() to req.amount
                        }
                    }
                }
            }
        }
    }

    override fun fromStorage(
        config: ConfigurationSection,
        storage: Storage
    ) = config.read {
        unlockableFactory.create(
            get("seriesId")!!,
            get("order")!!,
            get("message")!!,
            get("unlockedMessage")!!,
            section("requirements") {
                mapKeys {
                    if (it == "CURRENCY") {
                        Unlockable.Requirement.Economy(get(it)!!)
                    } else {
                        Unlockable.Requirement.Item(Material.getMaterial(it)!!, get(it)!!)
                    }
                }
            }!!
        )
    }
}