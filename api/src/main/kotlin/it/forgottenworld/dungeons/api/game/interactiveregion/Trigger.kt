package it.forgottenworld.dungeons.api.game.interactiveregion

import it.forgottenworld.dungeons.api.game.instance.DungeonInstance
import it.forgottenworld.dungeons.api.math.Vector3i
import it.forgottenworld.dungeons.api.storage.Storage
import org.bukkit.entity.Player

interface Trigger : InteractiveRegion, Storage.Storable {
    val effectCode: List<String>
    var label: String?
    val origin: Vector3i
    val requiresWholeParty: Boolean

    fun containsXYZ(x: Int, y: Int, z: Int): Boolean
    fun executeEffect(instance: DungeonInstance)
    fun debugLogEnter(player: Player)
    fun debugLogExit(player: Player)
}