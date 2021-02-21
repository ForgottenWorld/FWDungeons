package it.forgottenworld.dungeons.api.game.interactiveregion

import it.forgottenworld.dungeons.api.game.instance.DungeonInstance
import org.bukkit.Location
import org.bukkit.Material

interface ActiveArea : InteractiveRegion {
    val startingMaterial: Material
    var label: String?
    fun fillWithMaterial(material: Material, instance: DungeonInstance)
    fun getRandomLocationOnFloor(dungeonInstance: DungeonInstance): Location
}