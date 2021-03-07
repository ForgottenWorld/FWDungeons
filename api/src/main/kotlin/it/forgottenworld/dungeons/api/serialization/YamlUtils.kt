package it.forgottenworld.dungeons.api.serialization

import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.serialization.ConfigurationSerializable

inline fun ConfigurationSection.forEachSection(action: (String, ConfigurationSection) -> Unit) {
    for (k in getKeys(false)) {
        action(k, getConfigurationSection(k)!!)
    }
}

inline fun ConfigurationSection.edit(
    edit: YamlEditScope.() -> Unit
) = YamlEditScope(this).edit()

class YamlEditScope(val configurationSection: ConfigurationSection) {

    infix fun String.to(value: Any) = configurationSection.set(this, value)

    fun section(path: String) = configurationSection.createSection(path)

    inline fun section(
        path: String,
        edit: YamlEditScope.() -> Unit
    ) = configurationSection.createSection(path).apply { edit(edit) }
}

inline fun <R> ConfigurationSection.read(
    transform: YamlReadScope.() -> R
) = YamlReadScope(this).transform()

class YamlReadScope(val configurationSection: ConfigurationSection) {

    inline fun <reified T : Any> get(key: String) = configurationSection.get(key) as T?

    inline fun <reified T : ConfigurationSerializable> getSerializable(key: String) =
        configurationSection.getSerializable(key, T::class.java)

    inline fun <reified T : Any> get(key: String, def: T) =
        configurationSection.get(key) as T? ?: def

    inline fun <K, V> associateSections(
        transform: (String, ConfigurationSection) -> Pair<K, V>
    ) = configurationSection.getKeys(false).associate {
        transform(it, configurationSection.getConfigurationSection(it)!!)
    }

    inline fun <T> mapKeys(
        transform: (String) -> T
    ) = configurationSection.getKeys(false).map(transform)

    inline fun <K, reified V : Any> toMap(
        keyTransform: (String) -> K
    ) = configurationSection.getKeys(false).associate {
        keyTransform(it) to get<V>(it)!!
    }

    inline fun <T> mapSections(
        transform: (String, ConfigurationSection) -> T
    ) = configurationSection.getKeys(false).map {
        transform(it, configurationSection.getConfigurationSection(it)!!)
    }

    fun section(path: String) = configurationSection.getConfigurationSection(path)

    inline fun <R> section(
        path: String,
        transform: YamlReadScope.() -> R
    ) = configurationSection.getConfigurationSection(path)?.read(transform)
}