package it.forgottenworld.dungeons.core.config

import com.google.inject.Inject
import com.google.inject.Singleton
import it.forgottenworld.dungeons.api.game.chest.Chest
import it.forgottenworld.dungeons.api.game.dungeon.Dungeon
import it.forgottenworld.dungeons.api.game.dungeon.FinalDungeon
import it.forgottenworld.dungeons.api.game.dungeon.instance.DungeonInstance
import it.forgottenworld.dungeons.api.game.interactiveregion.ActiveArea
import it.forgottenworld.dungeons.api.game.interactiveregion.Trigger
import it.forgottenworld.dungeons.api.game.unlockables.Unlockable
import it.forgottenworld.dungeons.api.game.unlockables.UnlockableSeries
import it.forgottenworld.dungeons.api.math.Box
import it.forgottenworld.dungeons.api.math.BoxStorageStrategy
import it.forgottenworld.dungeons.api.math.Vector3i
import it.forgottenworld.dungeons.api.math.Vector3iStorageStrategy
import it.forgottenworld.dungeons.api.storage.Storage
import it.forgottenworld.dungeons.core.FWDungeonsPlugin
import it.forgottenworld.dungeons.core.game.chest.ChestStorageStrategy
import it.forgottenworld.dungeons.core.game.dungeon.FinalDungeonStorageStrategy
import it.forgottenworld.dungeons.core.game.dungeon.instance.DungeonInstanceStorageStrategy
import it.forgottenworld.dungeons.core.game.interactiveregion.activearea.ActiveAreaStorageStrategy
import it.forgottenworld.dungeons.core.game.interactiveregion.trigger.TriggerStorageStrategy
import it.forgottenworld.dungeons.core.game.unlockables.UnlockableSeriesStorageStrategy
import it.forgottenworld.dungeons.core.game.unlockables.UnlockableStorageStrategy
import org.bukkit.configuration.ConfigurationSection
import java.io.File
import kotlin.reflect.KClass

@Singleton
class StorageImpl @Inject constructor(
    finalDungeonStorageStrategy: FinalDungeonStorageStrategy,
    activeAreaStorageStrategy: ActiveAreaStorageStrategy,
    triggerStorageStrategy: TriggerStorageStrategy,
    chestStorageStrategy: ChestStorageStrategy,
    unlockableStorageStrategy: UnlockableStorageStrategy,
    unlockableSeriesImplStorageStrategy: UnlockableSeriesStorageStrategy,
    dungeonInstanceStorageStrategy: DungeonInstanceStorageStrategy,
    boxStorageStrategy: BoxStorageStrategy,
    vector3iStorageStrategy: Vector3iStorageStrategy,
    private val plugin: FWDungeonsPlugin
) : Storage {

    private val dungeonNameRegex = """[0-9]+\.yml""".toRegex()

    private val storageStragies = mapOf<KClass<*>, Storage.StorageStrategy<*>>(
        Dungeon::class to finalDungeonStorageStrategy,
        ActiveArea::class to activeAreaStorageStrategy,
        Trigger::class to triggerStorageStrategy,
        Chest::class to chestStorageStrategy,
        Unlockable::class to unlockableStorageStrategy,
        UnlockableSeries::class to unlockableSeriesImplStorageStrategy,
        DungeonInstance::class to dungeonInstanceStorageStrategy,
        Box::class to boxStorageStrategy,
        Vector3i::class to vector3iStorageStrategy
    )

    private val dungeonsFolder get() =
        File(plugin.dataFolder, "dungeons").apply {
            if (!exists()) mkdir()
        }

    override val intancesFile get() = File(
        plugin.dataFolder,
        "instances.yml"
    ).apply {
        if (!exists()) createNewFile()
    }

    override val unlockablesFile get() = File(
        plugin.dataFolder,
        "unlockables.yml"
    ).apply {
        if (!exists()) createNewFile()
    }

    override val dungeonFiles get() = dungeonsFolder
        .listFiles()!!
        .filter { it.name.matches(dungeonNameRegex) }

    override fun getFileForDungeon(dungeon: FinalDungeon) =
        File(dungeonsFolder,"${dungeon.id}.yml")

    @Suppress("UNCHECKED_CAST")
    override fun <T : Storage.Storable> load(klass: KClass<T>, config: ConfigurationSection): T =
        (storageStragies[klass] as Storage.StorageStrategy<T>)
            .fromStorage(config, this)

    @Suppress("UNCHECKED_CAST")
    override fun <T : Storage.Storable> save(klass: KClass<T>, storable: T, config: ConfigurationSection) {
        (storageStragies[klass] as Storage.StorageStrategy<T>).toStorage(
            storable,
            config,
            this
        )
    }
}