package it.forgottenworld.dungeons.model

import it.forgottenworld.dungeons.FWDungeonsPlugin
import it.forgottenworld.dungeons.config.ConfigManager
import it.forgottenworld.dungeons.manager.DungeonManager.collidingTrigger
import it.forgottenworld.dungeons.manager.DungeonManager.dungeonInstance
import it.forgottenworld.dungeons.manager.DungeonManager.party
import it.forgottenworld.dungeons.utils.sendFWDMessage
import net.md_5.bungee.api.ChatColor
import org.bukkit.entity.Player
import org.bukkit.metadata.FixedMetadataValue
import org.bukkit.util.BlockVector

class Trigger(
        val id: Int,
        val dungeon: Dungeon,
        val box: Box,
        val effect: ((DungeonInstance) -> Unit)?,
        val requiresWholeParty: Boolean = false) {

    var label: String? = null
    var procced = false

    private val playersCurrentlyInside = mutableListOf<Player>()
    val origin : BlockVector
        get() = box.origin

    fun applyMeta() =
        box.getAllBlocks().forEach {
            it.removeMetadata("FWD_triggers", FWDungeonsPlugin.instance)
            it.setMetadata(
                    "FWD_triggers",
                    FixedMetadataValue(FWDungeonsPlugin.instance, id)
            )
        }

    fun clearCurrentlyInsidePlayers() = playersCurrentlyInside.clear()

    fun isPlayerInside(player: Player) = box.containsPlayer(player)

    fun onPlayerEnter(player: Player) {
        if (ConfigManager.isInDebugMode)
            player.sendFWDMessage("Entered trigger ${ChatColor.DARK_GREEN}${label?.plus(" ") ?: ""}(id: $id)${ChatColor.WHITE} in dungeon ${ChatColor.GOLD}(id: ${dungeon.id})")

        if (playersCurrentlyInside.contains(player)) return

        player.collidingTrigger = this
        playersCurrentlyInside.add(player)
        proc(player.dungeonInstance!!)
    }

    fun onPlayerExit(player: Player) {
        if (ConfigManager.isInDebugMode)
            player.sendFWDMessage("Exited trigger ${ChatColor.DARK_GREEN}${label?.plus(" ") ?: ""}(id: $id)${ChatColor.WHITE} in dungeon ${ChatColor.GOLD}(id: ${dungeon.id})")

        playersCurrentlyInside.remove(player)
        player.collidingTrigger = null
    }

    private fun proc(instance: DungeonInstance) {
        if (playersCurrentlyInside.isEmpty()
                || procced
                || requiresWholeParty
                && playersCurrentlyInside[0].party?.playerCount != playersCurrentlyInside.count()) return

        procced = true
        effect?.invoke(instance)
    }
}