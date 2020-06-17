package it.forgottenworld.dungeons.model.trigger

import it.forgottenworld.dungeons.config.ConfigManager
import it.forgottenworld.dungeons.state.DungeonState
import it.forgottenworld.dungeons.cui.StringConst
import it.forgottenworld.dungeons.cui.getString
import it.forgottenworld.dungeons.model.box.Box
import it.forgottenworld.dungeons.model.dungeon.Dungeon
import it.forgottenworld.dungeons.model.dungeon.DungeonInstance
import it.forgottenworld.dungeons.utils.getParty
import net.md_5.bungee.api.ChatColor
import org.bukkit.entity.Player
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

    fun clearCurrentlyInsidePlayers() {
        playersCurrentlyInside.clear()
    }

    fun isPlayerInside(player: Player) = box.containsPlayer(player)

    fun onPlayerEnter(player: Player) {
        if (!playersCurrentlyInside.contains(player)) {
            DungeonState
                    .playersTriggering[player.uniqueId] = this
            if (ConfigManager.isInDebugMode)
                player.sendMessage("${getString(StringConst.CHAT_PREFIX)}Entered trigger ${ChatColor.DARK_GREEN}${label?.plus(" ") ?: ""}(id: $id)${ChatColor.WHITE} in dungeon ${ChatColor.GOLD}(id: ${dungeon.id})")
            playersCurrentlyInside.add(player)
            proc()
        }
    }

    fun onPlayerExit(player: Player) {
        if (ConfigManager.isInDebugMode)
            player.sendMessage("${getString(StringConst.CHAT_PREFIX)}Exited trigger ${ChatColor.DARK_GREEN}${label?.plus(" ") ?: ""}(id: $id)${ChatColor.WHITE} in dungeon ${ChatColor.GOLD}(id: ${dungeon.id})")
        playersCurrentlyInside.remove(player)
        DungeonState.playersTriggering.remove(player.uniqueId)
    }

    fun parseEffect(instance: DungeonInstance) {
        effectParser?.invoke(instance)?.let { effect = it }
    }

    private fun proc() {
        if (playersCurrentlyInside.isEmpty() || procced) return
        if (requiresWholeParty && playersCurrentlyInside[0].getParty()?.playerCount != playersCurrentlyInside.count())
            return
        playersCurrentlyInside[0].getParty()?.let {
            procced = true
            effect()
        }
    }
}