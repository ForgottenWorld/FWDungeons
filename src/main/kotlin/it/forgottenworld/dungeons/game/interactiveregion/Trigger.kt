package it.forgottenworld.dungeons.game.interactiveregion

import it.forgottenworld.dungeons.config.ConfigManager
import it.forgottenworld.dungeons.config.Strings
import it.forgottenworld.dungeons.game.box.Box
import it.forgottenworld.dungeons.game.dungeon.Dungeon
import it.forgottenworld.dungeons.game.dungeon.EditableDungeon.Companion.editableDungeon
import it.forgottenworld.dungeons.game.instance.DungeonFinalInstance
import it.forgottenworld.dungeons.game.instance.DungeonFinalInstance.Companion.finalInstance
import it.forgottenworld.dungeons.game.instance.DungeonInstance
import it.forgottenworld.dungeons.game.interactiveregion.Trigger.ActivationHandler.Companion.collidingTrigger
import it.forgottenworld.dungeons.scripting.CodeParser
import it.forgottenworld.dungeons.utils.sendFWDMessage
import it.forgottenworld.dungeons.utils.toVector
import org.bukkit.Bukkit
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.util.BlockVector
import org.bukkit.util.Vector
import java.util.*
import kotlin.reflect.KProperty
import org.bukkit.event.Event as BukkitEvent

class Trigger(
    override val id: Int,
    override val box: Box,
    val effectCode: List<String> = listOf(),
    private val requiresWholeParty: Boolean = false,
) : InteractiveRegion {

    private val effect = CodeParser.parseScript(effectCode)

    var label: String? = null
    private var procced = false

    private val playersCurrentlyInside = mutableSetOf<UUID>()
    val origin: BlockVector
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

    @Suppress("unused")
    class Event(
        val playerUuid: UUID,
        val triggerId: Int,
        val erase: Boolean
    ) : BukkitEvent() {

        override fun getHandlers() = HANDLERS

        companion object {
            private val HANDLERS = HandlerList()

            @JvmStatic
            fun getHandlerList() = HANDLERS
        }
    }

    class ActivationHandler : Listener {

        @EventHandler
        fun onTrigger(event: Event) {
            val player = Bukkit.getPlayer(event.playerUuid) ?: return
            if (event.erase) player.collidingTrigger?.onPlayerExit(player)
            if (event.triggerId == -1) return
            val inst = player.finalInstance
                ?: player.editableDungeon?.testInstance
                ?: return
            inst.triggers[event.triggerId]?.onPlayerEnter(player, inst)
        }

        companion object {

            private val triggerCollisions = mutableMapOf<UUID, Trigger>()

            fun clearAllCollisions() = triggerCollisions.clear()

            var Player.collidingTrigger
                get() = triggerCollisions[uniqueId]
                set(value) {
                    value?.let { triggerCollisions[uniqueId] = it }
                        ?: triggerCollisions.remove(uniqueId)
                }

            var UUID.collidingTrigger
                get() = triggerCollisions[this]
                set(value) {
                    value?.let { triggerCollisions[this] = it }
                        ?: triggerCollisions.remove(this)
                }
        }
    }

    class FinalInstanceTriggerDelegate private constructor(
        dungeon: Dungeon,
        newOrigin: BlockVector
    ) {

        private val triggers = dungeon
            .triggers
            .entries
            .associate { (k, v) -> k to v.withContainerOrigin(BlockVector(0, 0, 0), newOrigin) }

        operator fun getValue(thisRef: Any?, property: KProperty<*>) = triggers

        companion object {
            fun DungeonFinalInstance.instanceTriggers() = FinalInstanceTriggerDelegate(dungeon, origin)
        }
    }

    companion object {

        fun fromConfig(id: Int, config: ConfigurationSection) =
            Trigger(
                id,
                Box.fromConfig(config),
                CodeParser.cleanupCode(config.getString("effect")!!),
                config.getBoolean("requiresWholeParty")
            ).apply { config.getString("label")?.let { label = it } }
    }
}