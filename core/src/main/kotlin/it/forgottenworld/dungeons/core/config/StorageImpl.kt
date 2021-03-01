package it.forgottenworld.dungeons.core.config

import com.google.inject.Inject
import com.google.inject.Singleton
import it.forgottenworld.dungeons.api.game.chest.Chest
import it.forgottenworld.dungeons.api.game.dungeon.Dungeon
import it.forgottenworld.dungeons.api.game.instance.DungeonInstance
import it.forgottenworld.dungeons.api.game.interactiveregion.ActiveArea
import it.forgottenworld.dungeons.api.game.interactiveregion.Trigger
import it.forgottenworld.dungeons.api.game.unlockables.Unlockable
import it.forgottenworld.dungeons.api.game.unlockables.UnlockableSeries
import it.forgottenworld.dungeons.api.storage.Storage
import it.forgottenworld.dungeons.core.game.chest.ChestImplStorageStrategy
import it.forgottenworld.dungeons.core.game.dungeon.FinalDungeonStorageStrategy
import it.forgottenworld.dungeons.core.game.instance.DungeonInstanceStorageStrategy
import it.forgottenworld.dungeons.core.game.interactiveregion.ActiveAreaImplStorageStrategy
import it.forgottenworld.dungeons.core.game.interactiveregion.TriggerImplStorageStrategy
import it.forgottenworld.dungeons.core.game.unlockables.UnlockableSeriesImplStorageStrategy
import it.forgottenworld.dungeons.core.game.unlockables.UnlockableStorageStrategy
import org.bukkit.configuration.ConfigurationSection
import kotlin.reflect.KClass

@Suppress("UNCHECKED_CAST")
@Singleton
class StorageImpl @Inject constructor(
    finalDungeonStorageStrategy: FinalDungeonStorageStrategy,
    activeAreaImplStorageStrategy: ActiveAreaImplStorageStrategy,
    triggerImplStorageStrategy: TriggerImplStorageStrategy,
    chestImplStorageStrategy: ChestImplStorageStrategy,
    unlockableStorageStrategy: UnlockableStorageStrategy,
    unlockableSeriesImplStorageStrategy: UnlockableSeriesImplStorageStrategy,
    dungeonInstanceStorageStrategy: DungeonInstanceStorageStrategy
) : Storage {

    private val storageStragies = mapOf<KClass<*>, Storage.StorageStrategy<*>>(
        Dungeon::class to finalDungeonStorageStrategy,
        ActiveArea::class to activeAreaImplStorageStrategy,
        Trigger::class to triggerImplStorageStrategy,
        Chest::class to chestImplStorageStrategy,
        Unlockable::class to unlockableStorageStrategy,
        UnlockableSeries::class to unlockableSeriesImplStorageStrategy,
        DungeonInstance::class to dungeonInstanceStorageStrategy
    )

    override fun <T : Storage.Storable> load(klass: KClass<T>, config: ConfigurationSection): T =
        (storageStragies[klass] as Storage.StorageStrategy<T>)
            .fromConfig(config, this)

    override fun <T : Storage.Storable> save(storable: T, config: ConfigurationSection) {
        (storageStragies[storable::class] as Storage.StorageStrategy<T>).toConfig(
            storable,
            config,
            this
        )
    }
}