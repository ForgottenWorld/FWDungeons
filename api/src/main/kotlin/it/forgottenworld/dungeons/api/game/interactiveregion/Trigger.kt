package it.forgottenworld.dungeons.api.game.interactiveregion

import it.forgottenworld.dungeons.api.game.dungeon.instance.DungeonInstance
import it.forgottenworld.dungeons.api.storage.Storage
import org.bukkit.entity.Player

interface Trigger : InteractiveRegion, Storage.Storable {

    val effectCode: List<String>

    val requiresWholeParty: Boolean

    fun containsXYZ(x: Int, y: Int, z: Int): Boolean

    fun executeEffect(instance: DungeonInstance)

    fun debugLogEnter(player: Player)

    fun debugLogExit(player: Player)
}