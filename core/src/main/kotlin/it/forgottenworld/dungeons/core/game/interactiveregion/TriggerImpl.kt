package it.forgottenworld.dungeons.core.game.interactiveregion

import it.forgottenworld.dungeons.api.game.instance.DungeonInstance
import it.forgottenworld.dungeons.api.game.interactiveregion.Trigger
import it.forgottenworld.dungeons.api.math.Box
import it.forgottenworld.dungeons.api.math.Vector3i
import it.forgottenworld.dungeons.core.config.Strings
import it.forgottenworld.dungeons.core.game.instance.DungeonInstanceImpl
import it.forgottenworld.dungeons.core.scripting.CodeParser
import it.forgottenworld.dungeons.core.utils.sendFWDMessage
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player

data class TriggerImpl(
    override val id: Int,
    override val box: Box,
    override val effectCode: List<String> = listOf(),
    override val requiresWholeParty: Boolean = false,
    override var label: String? = null
) : Trigger {

    private val effect = CodeParser.parseScript(effectCode)

    override val origin: Vector3i
        get() = box.origin

    override fun containsXYZ(x: Int, y: Int, z: Int) = box.containsXYZ(x, y, z)

    fun debugLogEnter(player: Player) {
        player.sendFWDMessage(
            Strings.DEBUG_ENTERED_TRIGGER
                .format(label?.plus(" ") ?: "", id)
        )
    }

    fun debugLogExit(player: Player) {
        player.sendFWDMessage(
            Strings.DEBUG_EXITED_TRIGGER
                .format(label?.plus(" ") ?: "", id)
        )
    }

    override fun withContainerOrigin(oldOrigin: Vector3i, newOrigin: Vector3i) = copy(
        box = box.withContainerOrigin(oldOrigin, newOrigin)
    )

    override fun withContainerOriginZero(oldOrigin: Vector3i) = copy(
        box = box.withContainerOriginZero(oldOrigin)
    )

    override fun proc(instance: DungeonInstance) {
        if (instance !is DungeonInstanceImpl) return
        if (instance.proccedTriggers.contains(id) ||
            requiresWholeParty &&
            instance.playerTriggers.values.count { it == id } != instance.playerCount
        ) return
        instance.proccedTriggers.add(id)
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

        fun fromConfig(id: Int, config: ConfigurationSection) = TriggerImpl(
            id,
            Box.fromConfig(config),
            CodeParser.cleanupCode(config.getString("effect")!!),
            config.getBoolean("requiresWholeParty"),
            config.getString("label")
        )
    }
}