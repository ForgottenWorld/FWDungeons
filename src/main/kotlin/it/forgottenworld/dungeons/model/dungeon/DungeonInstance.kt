package it.forgottenworld.dungeons.model.dungeon

import it.forgottenworld.dungeons.model.activearea.ActiveArea
import it.forgottenworld.dungeons.model.box.Box
import it.forgottenworld.dungeons.model.party.Party
import it.forgottenworld.dungeons.model.trigger.Trigger
import it.forgottenworld.dungeons.utils.withRefSystemOrigin
import org.bukkit.Location
import org.bukkit.util.BlockVector

class DungeonInstance(
        val id: Int,
        val dungeon: Dungeon,
        val origin: BlockVector,
        val triggers: List<Trigger>,
        val activeAreas: List<ActiveArea>) {

    var party: Party? = null

    val box: Box
        get() = dungeon.box.withOrigin(origin)

    val startingPostion: BlockVector
        get() = dungeon.startingLocation.withRefSystemOrigin(BlockVector(0,0,0), origin)

    private val resolvedTriggers = mutableMapOf<Trigger, Boolean>().apply {
        dungeon.triggers.forEach {
            put(it, false)
        }
    }

    fun isInstanceBusy() = party != null

    fun resetInstance() {
        party = null
        resolvedTriggers.forEach{ resolvedTriggers[it.key] = false }
        activeAreas.forEach { it.fillWithMaterial(it.startingMaterial) }
    }
}