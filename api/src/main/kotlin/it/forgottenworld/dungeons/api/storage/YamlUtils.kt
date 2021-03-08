package it.forgottenworld.dungeons.api.storage

import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.YamlConfiguration

inline fun ConfigurationSection.edit(
    edit: YamlEditScope.() -> Unit
) = YamlEditScope(this).edit()

inline fun <R> ConfigurationSection.read(
    transform: YamlReadScope.() -> R
) = YamlReadScope(this).transform()

inline fun yaml(edit: YamlConfiguration.() -> Unit) = YamlConfiguration().apply(edit)