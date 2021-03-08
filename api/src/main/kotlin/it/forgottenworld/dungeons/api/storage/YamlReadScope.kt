package it.forgottenworld.dungeons.api.storage

import org.bukkit.configuration.ConfigurationSection

class YamlReadScope(val configurationSection: ConfigurationSection) {

    inline fun <reified T : Any> get(key: String) = configurationSection.get(key) as T?

    inline fun <reified T : Any> get(key: String, def: T) =
        configurationSection.get(key) as T? ?: def

    inline fun forEachSection(action: (String, ConfigurationSection) -> Unit) {
        for (k in configurationSection.getKeys(false)) {
            action(k, configurationSection.getConfigurationSection(k)!!)
        }
    }

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