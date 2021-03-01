package it.forgottenworld.dungeons.core.utils

import com.google.inject.Inject
import com.google.inject.Singleton
import it.forgottenworld.dungeons.core.FWDungeonsPlugin
import org.bukkit.NamespacedKey

@Singleton
class NamespacedKeys @Inject constructor(
    private val plugin: FWDungeonsPlugin
) {
    val triggerTool by lazy { NamespacedKey(plugin, "FWD_TRIGGER_WAND") }
    val activeAreaTool by lazy { NamespacedKey(plugin, "FWD_ACTIVE_AREA_WAND") }
}