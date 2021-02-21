package it.forgottenworld.dungeons.core.game.dungeon

import it.forgottenworld.dungeons.api.game.dungeon.Dungeon
import it.forgottenworld.dungeons.api.game.interactiveregion.InteractiveRegion
import it.forgottenworld.dungeons.api.math.Box
import it.forgottenworld.dungeons.api.math.Vector3i
import it.forgottenworld.dungeons.core.config.ConfigManager
import it.forgottenworld.dungeons.core.config.Strings
import it.forgottenworld.dungeons.core.game.chest.ChestImpl
import it.forgottenworld.dungeons.core.game.interactiveregion.ActiveAreaImpl
import it.forgottenworld.dungeons.core.game.interactiveregion.TriggerImpl
import it.forgottenworld.dungeons.core.utils.NamespacedKeys
import it.forgottenworld.dungeons.core.utils.ParticleSpammer
import it.forgottenworld.dungeons.core.utils.firstMissing
import it.forgottenworld.dungeons.core.utils.highlightAll
import it.forgottenworld.dungeons.core.utils.launchAsync
import it.forgottenworld.dungeons.core.utils.minecraft
import it.forgottenworld.dungeons.core.utils.player
import it.forgottenworld.dungeons.core.utils.plugin
import it.forgottenworld.dungeons.core.utils.sendFWDMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.bukkit.Bukkit
import org.bukkit.Particle
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.persistence.PersistentDataType
import java.io.File
import java.util.*
import kotlin.properties.Delegates.observable

class EditableDungeon(editor: Player) : Dungeon {

    private val editor by player(editor)

    override var id = -1000
    override var name = "NEW DUNGEON"
    override var description = ""
    override var difficulty = Dungeon.Difficulty.MEDIUM
    override var numberOfPlayers = 1..2
    override var box: Box? = null
    override var startingLocation: Vector3i? = null
    override var points = 0

    lateinit var testOrigin: Vector3i
    val testBox by lazy { box!!.withOrigin(testOrigin) }

    override var triggers by observable(mapOf<Int, TriggerImpl>()) { _, _, newValue ->
        updateTriggers(newValue.values)
    }

    override var activeAreas by observable(mapOf<Int, ActiveAreaImpl>()) { _, _, newValue ->
        updateActiveAreas(newValue.values)
    }

    override var chests = mapOf<Int, ChestImpl>()

    val dungeonBoxBuilder = Box.Builder()
    val triggerBoxBuilder = Box.Builder()
    val activeAreaBoxBuilder = Box.Builder()
    var finalInstanceLocations = mutableListOf<Vector3i>()

    val hasTestBox get() = this::testOrigin.isInitialized

    fun finalize(): FinalDungeon {
        val newId = if (id == -1000) FinalDungeon.dungeons.keys.firstMissing() else id

        val finalDungeon = FinalDungeon(
            newId,
            name,
            description,
            difficulty,
            points,
            numberOfPlayers,
            box!!.clone(),
            startingLocation!!.copy(),
            triggers.toMap(),
            activeAreas.toMap(),
            chests,
            mapOf()
        )

        FinalDungeon.dungeons[newId] = finalDungeon

        try {
            val config = YamlConfiguration()
            val file = File(plugin.dataFolder, "instances.yml")
            if (file.exists()) config.load(file)
            val dgConf = config.createSection("$newId")
            finalDungeon.instances = finalInstanceLocations.withIndex().map { (k, v) ->
                dgConf.createSection("$k").run {
                    set("x", v.x)
                    set("y", v.y)
                    set("z", v.z)
                }
                k to finalDungeon.createInstance(
                    ConfigManager.dungeonWorld.getBlockAt(v.x, v.y, v.z)
                )
            }.toMap()
            @Suppress("BlockingMethodInNonBlockingContext")
            launchAsync { config.save(file) }
        } catch (e: Exception) {
            Bukkit.getLogger().warning(e.message)
        }

        onDestroy()
        return finalDungeon
    }

    fun labelInteractiveRegion(type: InteractiveRegion.Type, label: String, id: Int = -1) {
        if (type == InteractiveRegion.Type.TRIGGER) labelTrigger(label, id) else labelActiveArea(label, id)
    }

    fun unmakeInteractiveRegion(type: InteractiveRegion.Type, ieId: Int?) =
        if (type == InteractiveRegion.Type.TRIGGER) unmakeTrigger(ieId) else unmakeActiveArea(ieId)

    suspend fun newInteractiveRegion(type: InteractiveRegion.Type, box: Box) =
        if (type == InteractiveRegion.Type.TRIGGER) newTrigger(box) else newActiveArea(box)

    private fun newActiveArea(box: Box): Int {
        val id = activeAreas.keys.lastOrNull()?.plus(1) ?: 0
        ActiveAreaImpl(
            id,
            box.withContainerOrigin(
                testOrigin,
                Vector3i(0, 0, 0)
            )
        ).let {
            activeAreas = activeAreas.plus(id to it)
            highlightNewInteractiveRegion(it)
        }
        return id
    }

    private fun unmakeActiveArea(aaId: Int?) =
        if (aaId == null) {
            activeAreas.keys.last().also { activeAreas = activeAreas.minus(it) }
        } else {
            activeAreas = activeAreas.minus(aaId)
            aaId
        }

    private fun labelActiveArea(label: String, id: Int = -1) {
        if (id == -1) {
            activeAreas.values.lastOrNull()?.label = label
        } else {
            activeAreas[id]?.label = label
        }
    }

    private suspend fun newTrigger(box: Box): Int {
        val id = triggers.keys.lastOrNull()?.plus(1) ?: 0

        return withContext(Dispatchers.minecraft) {
            TriggerImpl(
                id,
                box.withContainerOrigin(
                    testOrigin,
                    Vector3i(0, 0, 0)
                )
            ).let {
                highlightNewInteractiveRegion(it)
                triggers = triggers.plus(id to it)
            }
            id
        }
    }

    private fun unmakeTrigger(tId: Int?) =
        if (tId == null) {
            triggers.keys.last().also { triggers = triggers.minus(it) }
        } else {
            triggers = triggers.minus(tId)
            tId
        }

    private fun labelTrigger(label: String, id: Int = -1) {
        if (id == -1) {
            triggers.values.lastOrNull()?.label = label
        } else {
            triggers[id]?.label = label
        }
    }

    fun setupTestBox() {
        testOrigin = finalInstanceLocations.first()
    }

    fun onDestroy(restoreFormer: Boolean = false) {
        val player = editor ?: return
        stopParticleSpammers()
        dungeonBoxBuilder.clear()
        triggerBoxBuilder.clear()
        activeAreaBoxBuilder.clear()
        player.editableDungeon = null
        if (restoreFormer) FinalDungeon.dungeons[id]?.isBeingEdited = false
        player.sendFWDMessage(Strings.NO_LONGER_EDITING_DUNGEON)
    }

    fun whatIsMissingForWriteout() = StringBuilder().apply {
        if (!hasTestBox) append(Strings.WIM_BOX)
        if (startingLocation == null) append(Strings.WIM_STARTING_LOCATION)
        if (triggers.isEmpty()) append(Strings.WIM_AT_LEAST_ONE_TRIGGER)
        if (activeAreas.isEmpty()) append(Strings.WIM_AT_LEAST_ONE_ACTIVE_AREA)
    }.toString().dropLast(2)

    private var hlFrames = false

    private fun updateTriggers(newTriggers: Collection<TriggerImpl>) {
        updateTriggerParticleSpammers(newTriggers)
    }

    private fun updateActiveAreas(newActiveAreas: Collection<ActiveAreaImpl>) {
        updateActiveAreaParticleSpammers(newActiveAreas)
    }

    private fun highlightNewInteractiveRegion(interactiveRegion: InteractiveRegion) {
        interactiveRegion.withContainerOrigin(
            Vector3i(0, 0, 0),
            testOrigin
        ).also { it.box.highlightAll() }
    }

    private var triggerParticleSpammer: ParticleSpammer? = null
    private var activeAreaParticleSpammer: ParticleSpammer? = null

    private fun updateTriggerParticleSpammers(newTriggers: Collection<TriggerImpl>) {
        if (!hlFrames) return
        triggerParticleSpammer?.stop()
        triggerParticleSpammer = ParticleSpammer(
            Particle.DRIP_LAVA,
            1,
            500,
            newTriggers.flatMap {
                it.box.withContainerOrigin(Vector3i.ZERO, testOrigin).getFrame()
            }
        )
    }

    private fun updateActiveAreaParticleSpammers(newActiveAreas: Collection<ActiveAreaImpl>) {
        if (!hlFrames) return
        activeAreaParticleSpammer?.stop()
        activeAreaParticleSpammer = ParticleSpammer(
            Particle.DRIP_WATER,
            1,
            500,
            newActiveAreas.flatMap {
                it.box.withContainerOrigin(Vector3i.ZERO, testOrigin).getFrame()
            }
        )
    }

    private fun updateParticleSpammers() {
        if (!hlFrames) return
        updateTriggerParticleSpammers(triggers.values)
        updateActiveAreaParticleSpammers(activeAreas.values)
    }

    private fun stopParticleSpammers() {
        triggerParticleSpammer?.stop()
        triggerParticleSpammer = null
        activeAreaParticleSpammer?.stop()
        activeAreaParticleSpammer = null
    }

    fun toggleEditorHighlights() {
        hlFrames = !hlFrames
        if (hlFrames) {
            updateParticleSpammers()
        } else {
            stopParticleSpammers()
        }
    }

    fun handlePlayerInteract(event: PlayerInteractEvent) {
        val persistentDataContainer = event.item?.itemMeta?.persistentDataContainer ?: return

        val isTriggerWand = persistentDataContainer
            .get(NamespacedKeys.TRIGGER_TOOL, PersistentDataType.SHORT)
            ?.toShort() == 1.toShort()

        val isActiveAreaWand = !isTriggerWand && persistentDataContainer
            .get(NamespacedKeys.ACTIVE_AREA_TOOL, PersistentDataType.SHORT)
            ?.toShort() == 1.toShort()

        if (!isTriggerWand && !isActiveAreaWand) return

        val posNo = when (event.action) {
            Action.LEFT_CLICK_BLOCK -> 1
            Action.RIGHT_CLICK_BLOCK -> 2
            else -> return
        }

        val cmd = "fwde ${if (isTriggerWand) "trigger" else "activearea"} pos$posNo"

        event.player.performCommand(cmd)
        event.isCancelled = true
    }

    companion object {

        private val editableDungeons = mutableMapOf<UUID, EditableDungeon>()

        var Player.editableDungeon: EditableDungeon?
            get() = editableDungeons[uniqueId]
            set(value) {
                value?.let { editableDungeons[uniqueId] = it }
                    ?: editableDungeons.remove(uniqueId)
            }
    }
}