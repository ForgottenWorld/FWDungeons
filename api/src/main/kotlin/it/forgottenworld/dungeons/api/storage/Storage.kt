package it.forgottenworld.dungeons.api.storage

import it.forgottenworld.dungeons.api.game.dungeon.FinalDungeon
import org.bukkit.configuration.ConfigurationSection
import java.io.File
import kotlin.reflect.KClass

interface Storage {

    interface Storable

    val intancesFile: File

    val unlockablesFile: File

    val dungeonDataFolders: Map<Int, File>

    fun getConfigFileForDungeon(dungeon: FinalDungeon): File

    fun getScriptFilesForDungeon(dungeon: FinalDungeon): List<File>

    interface StorageStrategy<T : Storable> {
        fun toStorage(obj: T, config: ConfigurationSection, storage: Storage)
        fun fromStorage(config: ConfigurationSection, storage: Storage): T
    }

    fun <T : Storable> load(klass: KClass<T>, config: ConfigurationSection): T

    fun <T : Storable> save(klass: KClass<T>, storable: T, config: ConfigurationSection)

    companion object {
        inline fun <reified T : Storable> Storage.load(
            config: ConfigurationSection
        ): T = load(T::class, config)

        inline fun <reified T : Storable> Storage.save(
            storable: T,
            config: ConfigurationSection
        ) {
            save(T::class, storable, config)
        }
    }

    fun resetDungeonFolders()
}