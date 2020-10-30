package it.forgottenworld.dungeons.model.interactiveelement

import it.forgottenworld.dungeons.config.ConfigManager
import it.forgottenworld.dungeons.manager.DungeonManager.collidingTrigger
import it.forgottenworld.dungeons.model.box.Box
import it.forgottenworld.dungeons.model.instance.DungeonFinalInstance
import it.forgottenworld.dungeons.model.instance.DungeonInstance
import it.forgottenworld.dungeons.scripting.parseCode
import it.forgottenworld.dungeons.utils.ktx.sendFWDMessage
import it.forgottenworld.dungeons.utils.ktx.toVector
import net.md_5.bungee.api.ChatColor
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player
import org.bukkit.util.BlockVector
import org.bukkit.util.Vector
import java.util.*

class Trigger(
        override val id: Int,
        override val box: Box,
        private val effect: ((DungeonFinalInstance) -> Unit)? = null,
        private val requiresWholeParty: Boolean = false) : InteractiveElement {

    var label: String? = null
    var procced = false

    private val playersCurrentlyInside = mutableListOf<UUID>()
    val origin : BlockVector
        get() = box.origin

    fun clearCurrentlyInsidePlayers() = playersCurrentlyInside.clear()

    fun containsVector(vector: Vector) = box.containsVector(vector)

    fun onPlayerEnter(player: Player, instance: DungeonInstance) {
        if (ConfigManager.isInDebugMode)
            player.sendFWDMessage("Entered trigger ${ChatColor.DARK_GREEN}${label?.plus(" ") ?: ""}(id: $id)${ChatColor.WHITE}")

        if (playersCurrentlyInside.contains(player.uniqueId)) return

        player.collidingTrigger = this
        playersCurrentlyInside.add(player.uniqueId)
        if (instance is DungeonFinalInstance) proc(instance)
    }

    fun onPlayerExit(player: Player) {
        if (ConfigManager.isInDebugMode)
            player.sendFWDMessage("Exited trigger ${ChatColor.DARK_GREEN}${label?.plus(" ") ?: ""}(id: $id)${ChatColor.WHITE}")

        playersCurrentlyInside.remove(player.uniqueId)
        player.collidingTrigger = null
    }

    fun withContainerOrigin(oldOrigin: BlockVector, newOrigin: BlockVector) =
            Trigger(id,
                    box.withContainerOrigin(oldOrigin, newOrigin),
                    effect,
                    requiresWholeParty
            ).also { it.label = label }

    private fun proc(instance: DungeonFinalInstance) {
        if (procced || requiresWholeParty && instance.playerCount != playersCurrentlyInside.size) return
        procced = true
        effect?.invoke(instance)
    }

    fun toConfig(config: ConfigurationSection, eraseEffects: Boolean) = config.run {
        set("id", id)
        label?.let { l -> set("label", l) }
        set("origin", origin.toVector())
        set("width", box.width)
        set("height", box.height)
        set("depth", box.depth)
        if (eraseEffects) set("effect", getString("effect", ""))
        set("requiresWholeParty", requiresWholeParty)
    }
    
    companion object {
        fun fromConfig(id: Int, config: ConfigurationSection) =
                Trigger(
                        id,
                        Box.fromConfig(config),
                        parseCode(config.getStringList("effect")),
                        config.getBoolean("requiresWholeParty")
                ).apply { config.getString("label")?.let { label = it } }
    }
}