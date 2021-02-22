package it.forgottenworld.dungeons.core.utils

import org.bukkit.NamespacedKey

object NamespacedKeys {

    val TRIGGER_TOOL by lazy { NamespacedKey(plugin, "FWD_TRIGGER_WAND") }
    val ACTIVE_AREA_TOOL by lazy { NamespacedKey(plugin, "FWD_ACTIVE_AREA_WAND") }
}