package it.forgottenworld.dungeons.game.interactiveregion

import it.forgottenworld.dungeons.config.Strings
import it.forgottenworld.dungeons.game.box.Box
import it.forgottenworld.dungeons.game.instance.DungeonFinalInstance
import it.forgottenworld.dungeons.scripting.CodeParser
import it.forgottenworld.dungeons.utils.Vector3i
import it.forgottenworld.dungeons.utils.sendFWDMessage
import it.forgottenworld.dungeons.utils.toVector
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player

class Trigger(
    override val id: Int,
    override val box: Box,
    val effectCode: List<String> = listOf(),
    private val requiresWholeParty: Boolean = false,
    var label: String? = null
) : InteractiveRegion {

    private val effect = CodeParser.parseScript(effectCode)

    val origin: Vector3i
        get() = box.origin

    fun containsXYZ(x: Int, y: Int, z: Int) = box.containsXYZ(x, y, z)

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

    override fun withContainerOrigin(oldOrigin: Vector3i, newOrigin: Vector3i) = Trigger(
        id,
        box.withContainerOrigin(oldOrigin, newOrigin),
        effectCode,
        requiresWholeParty,
        label
    )

    fun proc(instance: DungeonFinalInstance) {
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

        fun fromConfig(id: Int, config: ConfigurationSection) = Trigger(
            id,
            Box.fromConfig(config),
            CodeParser.cleanupCode(config.getString("effect")!!),
            config.getBoolean("requiresWholeParty"),
            config.getString("label")
        )
    }
}