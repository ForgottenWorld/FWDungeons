package it.forgottenworld.dungeons.api.game.interactiveregion

import it.forgottenworld.dungeons.api.game.dungeon.instance.DungeonInstance
import it.forgottenworld.dungeons.api.storage.Storage
import org.bukkit.Material

interface ActiveArea : InteractiveRegion, Storage.Storable {

    val startingMaterial: Material

    fun fillWithMaterial(material: Material, instance: DungeonInstance)
}