package it.forgottenworld.dungeons.model.dungeon

import it.forgottenworld.dungeons.model.box.Box
import it.forgottenworld.dungeons.model.party.Party
import it.forgottenworld.dungeons.model.trigger.Trigger
import org.bukkit.util.BlockVector

class DungeonInstance(
        val id: Int,
        val dungeon: Dungeon,
        val origin: BlockVector,
        val triggers: List<Trigger>) {
    var party: Party? = null
    private val resolvedTriggers = mutableMapOf<Trigger, Boolean>().apply {
        dungeon.triggers.forEach {
            put(it, false)
        }
    }

    val box: Box
        get() = Box(origin, dungeon.box.width, dungeon.box.height, dungeon.box.depth)

    fun isInstanceBusy() = party != null

    fun startInstance(party: Party) {
        party.instance = this
        this.party = party
    }

    fun resetInstance() {
        party = null
        resolvedTriggers.forEach{
            resolvedTriggers[it.key] = false
        }
    }
}