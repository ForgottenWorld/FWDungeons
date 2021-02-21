package it.forgottenworld.dungeons.api.game.interactiveregion

import it.forgottenworld.dungeons.api.game.instance.DungeonInstance
import it.forgottenworld.dungeons.api.math.Vector3i

interface Trigger : InteractiveRegion {
    val effectCode: List<String>
    var label: String?
    val origin: Vector3i
    val requiresWholeParty: Boolean

    fun containsXYZ(x: Int, y: Int, z: Int): Boolean
    fun proc(instance: DungeonInstance)
}