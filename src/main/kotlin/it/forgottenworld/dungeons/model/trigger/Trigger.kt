package it.forgottenworld.dungeons.model.trigger

import it.forgottenworld.dungeons.FWDungeonsPlugin
import it.forgottenworld.dungeons.config.ConfigManager
import it.forgottenworld.dungeons.model.box.Box
import it.forgottenworld.dungeons.model.dungeon.Dungeon
import it.forgottenworld.dungeons.model.dungeon.DungeonInstance
import it.forgottenworld.dungeons.state.DungeonState.collidingTrigger
import it.forgottenworld.dungeons.state.DungeonState.party
import it.forgottenworld.dungeons.utils.sendFWDMessage
import net.md_5.bungee.api.ChatColor
import org.bukkit.entity.Player
import org.bukkit.metadata.FixedMetadataValue
import org.bukkit.util.BlockVector

class Trigger(
        val id: Int,
        val dungeon: Dungeon,
        val box: Box,
        val effectParser: ((DungeonInstance) -> () -> Unit)?,
        val requiresWholeParty: Boolean = false) {

    var label: String? = null
    var procced = false
    lateinit var effect: () -> Unit

    private val playersCurrentlyInside = mutableListOf<Player>()
    val origin : BlockVector
        get() = box.origin

    fun applyMeta(dungeonInstance: DungeonInstance) =
        box.getAllBlocks().forEach {
            it.setMetadata(
                    "FWD_triggers",
                    FixedMetadataValue(
                            FWDungeonsPlugin.instance,
                            "${dungeonInstance.id}-$id"
                    )
            )
        }

    fun clearCurrentlyInsidePlayers() = playersCurrentlyInside.clear()

    fun isPlayerInside(player: Player) = box.containsPlayer(player)

    fun onPlayerEnter(player: Player) {
        if (playersCurrentlyInside.contains(player)) return
        player.collidingTrigger = this
        if (ConfigManager.isInDebugMode)
            player.sendFWDMessage("Entered trigger ${ChatColor.DARK_GREEN}${label?.plus(" ") ?: ""}(id: $id)${ChatColor.WHITE} in dungeon ${ChatColor.GOLD}(id: ${dungeon.id})")
        playersCurrentlyInside.add(player)
        proc()
    }

    fun onPlayerExit(player: Player) {
        if (ConfigManager.isInDebugMode)
            player.sendFWDMessage("Exited trigger ${ChatColor.DARK_GREEN}${label?.plus(" ") ?: ""}(id: $id)${ChatColor.WHITE} in dungeon ${ChatColor.GOLD}(id: ${dungeon.id})")
        playersCurrentlyInside.remove(player)
        player.collidingTrigger = null
    }

    fun parseEffect(instance: DungeonInstance) {
        effectParser?.invoke(instance)?.let { effect = it }
    }

    private fun proc() {
        if (playersCurrentlyInside.isEmpty()
                || procced
                || requiresWholeParty
                && playersCurrentlyInside[0].party?.playerCount != playersCurrentlyInside.count()) return

        procced = true
        effect()
    }
}