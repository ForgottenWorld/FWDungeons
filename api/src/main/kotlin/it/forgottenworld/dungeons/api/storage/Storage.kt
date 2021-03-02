package it.forgottenworld.dungeons.api.storage

import org.bukkit.configuration.ConfigurationSection
import kotlin.reflect.KClass

interface Storage {

    interface Storable

    interface StorageStrategy<T : Storable> {
        fun toStorage(obj: T, config: ConfigurationSection, storage: Storage)
        fun fromStorage(config: ConfigurationSection, storage: Storage): T
    }

    fun <T : Storable> load(klass: KClass<T>, config: ConfigurationSection): T

    fun <T : Storable> save(storable: T, config: ConfigurationSection)

    companion object {
        inline fun <reified T : Storable> Storage.load(config: ConfigurationSection): T = load(T::class, config)
    }
}