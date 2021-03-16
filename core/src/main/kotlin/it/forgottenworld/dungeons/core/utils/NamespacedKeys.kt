package it.forgottenworld.dungeons.core.utils

import com.google.inject.Inject
import com.google.inject.Singleton
import it.forgottenworld.dungeons.core.FWDungeonsPlugin
import org.bukkit.NamespacedKey

@Singleton
class NamespacedKeys @Inject constructor(
    plugin: FWDungeonsPlugin
) {
    val triggerTool = NamespacedKey(plugin, "FWD_TRIGGER_WAND")
    val activeAreaTool = NamespacedKey(plugin, "FWD_ACTIVE_AREA_WAND")
    val spawnAreaTool = NamespacedKey(plugin, "FWD_SPAWN_AREA_WAND")
}