package it.forgottenworld.dungeons.model.interactiveelement

import it.forgottenworld.dungeons.config.ConfigManager
import it.forgottenworld.dungeons.event.listener.TriggerActivationHandler.Companion.collidingTrigger
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
        val effectCode: List<String> = listOf(),
        private val requiresWholeParty: Boolean = false,
) : InteractiveElement {

    private val effect = parseCode(effectCode)

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
                    effectCode,
                    requiresWholeParty
            ).also { it.label = label }

    private fun proc(instance: DungeonFinalInstance) {
        if (procced || requiresWholeParty && instance.playerCount != playersCurrentlyInside.size) return
        procced = true
        instance.unproccedTriggers.remove(this)
        effect.invoke(instance)
    }

    fun toConfig(config: ConfigurationSection) = config.run {
        set("id", id)
        label?.let { set("label", it) }
        set("origin", origin.toVector())
        set("width", box.width)
        set("height", box.height)
        set("depth", box.depth)
        set("effect", effectCode)
        set("requiresWholeParty", requiresWholeParty)
    }
    
    companion object {
        fun fromConfig(id: Int, config: ConfigurationSection) =
                Trigger(
                        id,
                        Box.fromConfig(config),
                        config.getStringList("effect"),
                        config.getBoolean("requiresWholeParty")
                ).apply { config.getString("label")?.let { label = it } }
    }
}