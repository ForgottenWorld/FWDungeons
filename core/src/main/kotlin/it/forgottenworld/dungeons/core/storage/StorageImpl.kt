package it.forgottenworld.dungeons.core.storage

import com.google.inject.Inject
import com.google.inject.Singleton
import it.forgottenworld.dungeons.api.game.dungeon.subelement.chest.Chest
import it.forgottenworld.dungeons.api.game.dungeon.FinalDungeon
import it.forgottenworld.dungeons.api.game.dungeon.instance.DungeonInstance
import it.forgottenworld.dungeons.api.game.dungeon.subelement.interactiveregion.ActiveArea
import it.forgottenworld.dungeons.api.game.dungeon.subelement.interactiveregion.SpawnArea
import it.forgottenworld.dungeons.api.game.dungeon.subelement.interactiveregion.Trigger
import it.forgottenworld.dungeons.api.game.unlockables.Unlockable
import it.forgottenworld.dungeons.api.game.unlockables.UnlockableSeries
import it.forgottenworld.dungeons.api.math.Box
import it.forgottenworld.dungeons.api.math.BoxStorageStrategy
import it.forgottenworld.dungeons.api.math.Vector3i
import it.forgottenworld.dungeons.api.math.Vector3iStorageStrategy
import it.forgottenworld.dungeons.api.storage.Storage
import it.forgottenworld.dungeons.core.FWDungeonsPlugin
import it.forgottenworld.dungeons.core.game.dungeon.subelement.chest.ChestStorageStrategy
import it.forgottenworld.dungeons.core.game.dungeon.FinalDungeonStorageStrategy
import it.forgottenworld.dungeons.core.game.dungeon.instance.DungeonInstanceStorageStrategy
import it.forgottenworld.dungeons.core.game.dungeon.subelement.interactiveregion.activearea.ActiveAreaStorageStrategy
import it.forgottenworld.dungeons.core.game.dungeon.subelement.interactiveregion.spawnarea.SpawnAreaStorageStrategy
import it.forgottenworld.dungeons.core.game.dungeon.subelement.interactiveregion.trigger.TriggerStorageStrategy
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
    spawnAreaStorageStrategy: SpawnAreaStorageStrategy,
    private val plugin: FWDungeonsPlugin
) : Storage {

    private val storageStragies = mapOf<KClass<*>, Storage.StorageStrategy<*>>(
        FinalDungeon::class to finalDungeonStorageStrategy,
        ActiveArea::class to activeAreaStorageStrategy,
        SpawnArea::class to spawnAreaStorageStrategy,
        Trigger::class to triggerStorageStrategy,
        Chest::class to chestStorageStrategy,
        Unlockable::class to unlockableStorageStrategy,
        UnlockableSeries::class to unlockableSeriesImplStorageStrategy,
        DungeonInstance::class to dungeonInstanceStorageStrategy,
        Box::class to boxStorageStrategy,
        Vector3i::class to vector3iStorageStrategy
    )

    private val dungeonsDirectory get() = File(
        plugin.dataFolder,
        "dungeons"
    ).apply {
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

    private var _dungeonDataFolders: Map<Int, File>? = null

    override val dungeonDataFolders: Map<Int, File>
        get() {
            _dungeonDataFolders?.let { return it }
            _dungeonDataFolders = dungeonsDirectory
                .listFiles()!!
                .filter { file -> file.isDirectory &&
                    file.name.startsWith("dungeon_") &&
                    file.listFiles()?.all {
                        it.name == "config.yml" || it.extension == "dgs"
                    } == true
                }.associateBy {
                    it.name.removePrefix("dungeon_").toInt()
                }
            return _dungeonDataFolders!!
        }

    override fun resetDungeonFolders() {
        _dungeonDataFolders = null
    }

    override fun getConfigFileForDungeon(dungeon: FinalDungeon): File {
        val folder = dungeonDataFolders[dungeon.id] ?: run {
            val newFolder = File(dungeonsDirectory, "dungeon_${dungeon.id}")
            newFolder.mkdir()
            resetDungeonFolders()
            newFolder
        }
        return File(folder,"config.yml")
    }

    override fun getScriptFilesForDungeon(dungeon: FinalDungeon) = dungeonDataFolders[dungeon.id]
        ?.listFiles()
        ?.filter { it.extension == "dgs" }
        ?: listOf()

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