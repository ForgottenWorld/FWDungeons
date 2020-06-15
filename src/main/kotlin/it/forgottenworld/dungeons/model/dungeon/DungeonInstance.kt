package it.forgottenworld.dungeons.model.dungeon

import it.forgottenworld.dungeons.controller.FWDungeonsController
import it.forgottenworld.dungeons.controller.MobTracker
import it.forgottenworld.dungeons.cui.StringConst
import it.forgottenworld.dungeons.cui.getString
import it.forgottenworld.dungeons.event.DungeonCompletedEvent
import it.forgottenworld.dungeons.model.activearea.ActiveArea
import it.forgottenworld.dungeons.model.box.Box
import it.forgottenworld.dungeons.model.party.Party
import it.forgottenworld.dungeons.model.trigger.Trigger
import it.forgottenworld.dungeons.utils.*
import net.md_5.bungee.api.ChatColor
import org.bukkit.Bukkit
import org.bukkit.Particle
import org.bukkit.util.BlockVector

class DungeonInstance(
        val id: Int,
        val dungeon: Dungeon,
        private val origin: BlockVector,
        val triggers: MutableList<Trigger>,
        val activeAreas: MutableList<ActiveArea>) {

    private val activeAreasIdMap = activeAreas.toActiveAreaIdMap()
    var highlightFrames = TypeWrapper(false)

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
            it.sendMessage("${getString(StringConst.CHAT_PREFIX)}${ChatColor.GREEN}Congratulations, you made it out alive!")
            FWDungeonsController.playerReturnPositions[it.uniqueId]?.let { pos -> it.teleport(pos) }
            FWDungeonsController.playerReturnGameModes[it.uniqueId]?.let { gm -> it.gameMode = gm }
        }
        party?.disband()
        resetInstance()
    }

    fun toggleEditorHighlights() {
        if (highlightFrames.value)
            highlightFrames.value = false
        else {
            highlightFrames.value = true
            val aaLocs = activeAreas.map { it.box.getFrontierBlocks() }.flatten().map { it.location }.toSet()
            val tLocs = triggers.map { it.box.getFrontierBlocks() }.flatten().map { it.location }.toSet()
            if (aaLocs.isNotEmpty())
                repeatedlySpawnParticles(
                        Particle.DRIP_WATER,
                        aaLocs,
                        1,
                        10,
                        highlightFrames
                )
            if (tLocs.isNotEmpty())
                repeatedlySpawnParticles(
                        Particle.DRIP_LAVA,
                        tLocs,
                        1,
                        10,
                        highlightFrames
                )
        }
    }
}