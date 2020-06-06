package it.forgottenworld.dungeons.model.dungeon

import it.forgottenworld.dungeons.model.activearea.ActiveArea
import it.forgottenworld.dungeons.model.box.Box
import it.forgottenworld.dungeons.model.party.Party
import it.forgottenworld.dungeons.model.trigger.Trigger
import it.forgottenworld.dungeons.utils.toActiveAreaIdMap
import it.forgottenworld.dungeons.utils.toTriggerIdMap
import it.forgottenworld.dungeons.utils.withRefSystemOrigin
import org.bukkit.util.BlockVector

class DungeonInstance(
        val id: Int,
        val dungeon: Dungeon,
        private val origin: BlockVector,
        val triggers: List<Trigger>,
        val activeAreas: List<ActiveArea>) {
    private val triggersIdMap = triggers.toTriggerIdMap()
    private val activeAreasIdMap = activeAreas.toActiveAreaIdMap()

    fun getTriggerById(id: Int) = triggersIdMap[id]
    fun getActiveAreaById(id: Int) = activeAreasIdMap[id]

    var party: Party? = null

    val box: Box
        get() = dungeon.box.withOrigin(origin)

    val startingPostion: BlockVector
        get() = dungeon.startingLocation.withRefSystemOrigin(BlockVector(0,0,0), origin)

    fun resetInstance() {
        party = null
        triggers.forEach{ it.procced = false }
        activeAreas.forEach { it.fillWithMaterial(it.startingMaterial) }
    }
}