package it.forgottenworld.dungeons.api.storage

import org.bukkit.configuration.ConfigurationSection

class YamlEditScope(val configurationSection: ConfigurationSection) {

    infix fun String.to(value: Any) = configurationSection.set(this, value)

    fun section(path: String) = configurationSection.createSection(path)

    inline fun section(
        path: String,
        edit: YamlEditScope.() -> Unit
    ) = configurationSection.createSection(path).apply { edit(edit) }
}