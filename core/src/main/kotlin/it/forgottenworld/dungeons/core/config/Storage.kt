package it.forgottenworld.dungeons.core.config

import it.forgottenworld.dungeons.core.game.chest.ChestImpl
import it.forgottenworld.dungeons.core.game.chest.ChestImplStorageStrategy
import it.forgottenworld.dungeons.core.game.dungeon.FinalDungeon
import it.forgottenworld.dungeons.core.game.dungeon.FinalDungeonStorageStrategy
import it.forgottenworld.dungeons.core.game.interactiveregion.ActiveAreaImpl
import it.forgottenworld.dungeons.core.game.interactiveregion.ActiveAreaImplStorageStrategy
import it.forgottenworld.dungeons.core.game.interactiveregion.TriggerImpl
import it.forgottenworld.dungeons.core.game.interactiveregion.TriggerImplStorageStrategy
import it.forgottenworld.dungeons.core.game.unlockables.UnlockableImpl
import it.forgottenworld.dungeons.core.game.unlockables.UnlockableImplStorageStrategy
import it.forgottenworld.dungeons.core.game.unlockables.UnlockableSeriesImpl
import it.forgottenworld.dungeons.core.game.unlockables.UnlockableSeriesImplStorageStrategy
import org.bukkit.configuration.ConfigurationSection
import kotlin.reflect.KClass

@Suppress("UNCHECKED_CAST")
object Storage {

    interface Storable

    interface StorageStrategy<T : Storable> {
        fun toConfig(obj: T, config: ConfigurationSection)
        fun fromConfig(config: ConfigurationSection): T
    }

    val storageStragies = mapOf<KClass<*>, StorageStrategy<out Storable>>(
        FinalDungeon::class to FinalDungeonStorageStrategy(),
        ActiveAreaImpl::class to ActiveAreaImplStorageStrategy(),
        TriggerImpl::class to TriggerImplStorageStrategy(),
        ChestImpl::class to ChestImplStorageStrategy(),
        UnlockableImpl::class to UnlockableImplStorageStrategy(),
        UnlockableSeriesImpl::class to UnlockableSeriesImplStorageStrategy()
    )

    inline fun <reified T: Storable> load(config: ConfigurationSection): T =
        (storageStragies[T::class] as StorageStrategy<out T>).fromConfig(config)

    fun <T: Storable> T.toConfig(config: ConfigurationSection) {
        (storageStragies[this::class] as StorageStrategy<T>).toConfig(this, config)
    }

}