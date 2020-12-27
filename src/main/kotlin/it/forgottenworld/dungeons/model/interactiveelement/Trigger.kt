package it.forgottenworld.dungeons.model.interactiveelement

import it.forgottenworld.dungeons.config.ConfigManager
import it.forgottenworld.dungeons.config.Strings
import it.forgottenworld.dungeons.event.listener.TriggerActivationHandler.Companion.collidingTrigger
import it.forgottenworld.dungeons.model.box.Box
import it.forgottenworld.dungeons.model.instance.DungeonFinalInstance
import it.forgottenworld.dungeons.model.instance.DungeonInstance
import it.forgottenworld.dungeons.scripting.cleanupCode
import it.forgottenworld.dungeons.scripting.parseScript
import it.forgottenworld.dungeons.utils.ktx.sendFWDMessage
import it.forgottenworld.dungeons.utils.ktx.toVector
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

    private val effect = parseScript(effectCode)

    var label: String? = null
    private var procced = false

    private val playersCurrentlyInside = mutableSetOf<UUID>()
    val origin : BlockVector
        get() = box.origin

    fun reset() {
        procced = false
        playersCurrentlyInside.forEach { it.collidingTrigger = null }
        playersCurrentlyInside.clear()
    }

    fun containsVector(vector: Vector) = box.containsVector(vector)

    fun onPlayerEnter(player: Player, instance: DungeonInstance) {
        if (ConfigManager.isDebugMode)
            player.sendFWDMessage(Strings.DEBUG_ENTERED_TRIGGER.format(label?.plus(" ") ?: "", id))

        if (playersCurrentlyInside.contains(player.uniqueId)) return

        player.collidingTrigger = this
        playersCurrentlyInside.add(player.uniqueId)
        if (instance is DungeonFinalInstance) proc(instance)
    }

    fun onPlayerExit(player: Player) {
        if (ConfigManager.isDebugMode)
            player.sendFWDMessage(Strings.DEBUG_EXITED_TRIGGER.format(label?.plus(" ") ?: "", id))

        playersCurrentlyInside.remove(player.uniqueId)
        player.collidingTrigger = null
    }

    override fun withContainerOrigin(oldOrigin: BlockVector, newOrigin: BlockVector) =
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
        set("effect", effectCode.joinToString("; "))
        set("requiresWholeParty", requiresWholeParty)
    }

    companion object {

        fun fromConfig(id: Int, config: ConfigurationSection) =
                Trigger(
                        id,
                        Box.fromConfig(config),
                        cleanupCode(config.getString("effect")!!),
                        config.getBoolean("requiresWholeParty")
                ).apply { config.getString("label")?.let { label = it } }
    }
}