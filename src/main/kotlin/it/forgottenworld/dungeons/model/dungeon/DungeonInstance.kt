package it.forgottenworld.dungeons.model.dungeon

import it.forgottenworld.dungeons.controller.FWDungeonsController
import it.forgottenworld.dungeons.controller.MobTracker
import it.forgottenworld.dungeons.event.DungeonCompletedEvent
import it.forgottenworld.dungeons.model.activearea.ActiveArea
import it.forgottenworld.dungeons.model.box.Box
import it.forgottenworld.dungeons.model.party.Party
import it.forgottenworld.dungeons.model.trigger.Trigger
import it.forgottenworld.dungeons.utils.toActiveAreaIdMap
import it.forgottenworld.dungeons.utils.toTriggerIdMap
import it.forgottenworld.dungeons.utils.withRefSystemOrigin
import net.md_5.bungee.api.ChatColor
import org.bukkit.Bukkit
import org.bukkit.util.BlockVector

class DungeonInstance(
        val id: Int,
        val dungeon: Dungeon,
        private val origin: BlockVector,
        val triggers: List<Trigger>,
        private val activeAreas: List<ActiveArea>) {
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
        triggers.forEach{
            it.procced = false
            it.clearCurrentlyInsidePlayers()
        }
        activeAreas.forEach { it.fillWithMaterial(it.startingMaterial) }
        MobTracker.instanceObjectives[id]?.abort()
        @Suppress("ControlFlowWithEmptyBody")
        while (MobTracker.instanceIdForTrackedMobs.values.remove(id)) {}
    }

    fun onInstanceFinish(givePoints: Boolean) {
        if (givePoints && dungeon.points != 0) {
            party?.players?.map { it.uniqueId.toString() }?.toSet()?.let {
                DungeonCompletedEvent(
                        it, dungeon.points.toFloat()
                )
            }?.apply {
                Bukkit.getPluginManager().callEvent(this)
            }
        }

        party?.players?.forEach {
            it.sendMessage("${ChatColor.GREEN}Congratulations, you made it out alive!")
            FWDungeonsController.playerReturnPositions[it.uniqueId]?.let { pos -> it.teleport(pos) }
            FWDungeonsController.playerReturnGameModes[it.uniqueId]?.let { gm -> it.gameMode = gm }
        }
        party?.disband()
        resetInstance()
    }
}