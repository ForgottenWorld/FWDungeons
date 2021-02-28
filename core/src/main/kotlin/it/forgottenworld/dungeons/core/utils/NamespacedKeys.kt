package it.forgottenworld.dungeons.core.utils

import it.forgottenworld.dungeons.core.FWDungeonsPlugin
import org.bukkit.NamespacedKey

object NamespacedKeys {

    val TRIGGER_TOOL by lazy { NamespacedKey(FWDungeonsPlugin.getInstance(), "FWD_TRIGGER_WAND") }
    val ACTIVE_AREA_TOOL by lazy { NamespacedKey(FWDungeonsPlugin.getInstance(), "FWD_ACTIVE_AREA_WAND") }
}