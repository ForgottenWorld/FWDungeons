package it.forgottenworld.dungeons.core.game.dungeon

import it.forgottenworld.dungeons.api.game.chest.Chest
import it.forgottenworld.dungeons.api.game.dungeon.Dungeon
import it.forgottenworld.dungeons.api.game.interactiveregion.ActiveArea
import it.forgottenworld.dungeons.api.game.interactiveregion.InteractiveRegion
import it.forgottenworld.dungeons.api.game.interactiveregion.Trigger
import it.forgottenworld.dungeons.api.math.Box
import it.forgottenworld.dungeons.api.math.Vector3i
import it.forgottenworld.dungeons.core.FWDungeonsPlugin
import it.forgottenworld.dungeons.core.config.Configuration
import it.forgottenworld.dungeons.core.config.Strings
import it.forgottenworld.dungeons.core.game.dungeon.DungeonManager.editableDungeon
import it.forgottenworld.dungeons.core.game.dungeon.DungeonManager.instances
import it.forgottenworld.dungeons.core.game.interactiveregion.ActiveAreaImpl
import it.forgottenworld.dungeons.core.game.interactiveregion.TriggerImpl
import it.forgottenworld.dungeons.core.utils.*
import org.bukkit.Bukkit
import org.bukkit.Particle
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.persistence.PersistentDataType
import java.io.File

class EditableDungeon(
    editor: Player,
    override var id: Int = NEW_DUNGEON_TEMP_ID,
    override var name: String = "NEW DUNGEON",
    override var description: String = "",
    override var difficulty: Dungeon.Difficulty = Dungeon.Difficulty.MEDIUM,
    override var minPlayers: Int = 1,
    override var maxPlayers: Int = 2,
    override var box: Box? = null,
    override var startingLocation: Vector3i? = null,
    override var points: Int = 0,
    var finalInstanceLocations: MutableList<Vector3i> = mutableListOf(),
    triggers: Map<Int, Trigger> = mutableMapOf(),
    activeAreas: Map<Int, ActiveArea> = mutableMapOf(),
    override var chests: MutableMap<Int, Chest> = mutableMapOf()
) : Dungeon {

    private val editor = editor.uniqueId

    lateinit var testOrigin: Vector3i

    init {
        if (finalInstanceLocations.isNotEmpty()) setupTestBox()
    }

    override var triggers = triggers
        private set(value) {
            field = value
            updateTriggerParticleSpammers(value.values)
        }

    override var activeAreas = activeAreas
        private set(value) {
            field = value
            updateActiveAreaParticleSpammers(value.values)
        }

    val dungeonBoxBuilder = Box.Builder()
    val triggerBoxBuilder = Box.Builder()
    val activeAreaBoxBuilder = Box.Builder()

    val hasTestOrigin get() = this::testOrigin.isInitialized

    private var hlFrames = false
    private var triggerParticleSpammer: ParticleSpammer? = null
    private var activeAreaParticleSpammer: ParticleSpammer? = null

    fun finalize(): FinalDungeon {
        val newId = if (id == NEW_DUNGEON_TEMP_ID ||
            DungeonManager.finalDungeons.containsKey(id)
        ) {
            DungeonManager.finalDungeons.keys.firstGap()
        } else {
            id
        }

        val finalDungeon = FinalDungeon(
            newId,
            name,
            description,
            difficulty,
            points,
            minPlayers,
            maxPlayers,
            box!!.clone(),
            startingLocation!!.copy(),
            triggers,
            activeAreas,
            chests
        )

        DungeonManager.finalDungeons[newId] = finalDungeon
        saveToConfigAndCreateInstances(newId, finalDungeon)
        onDestroy()
        return finalDungeon
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    private fun saveToConfigAndCreateInstances(newId: Int, finalDungeon: FinalDungeon) {
        try {
            val config = YamlConfiguration()
            val file = File(FWDungeonsPlugin.getInstance().dataFolder, "instances.yml")
            if (file.exists()) config.load(file)
            val dgConf = config.createSection("$newId")
            finalDungeon.instances = finalInstanceLocations.withIndex().map { (k, v) ->
                dgConf.createSection("$k").run {
                    set("x", v.x)
                    set("y", v.y)
                    set("z", v.z)
                }
                k to finalDungeon.createInstance(
                    Configuration.dungeonWorld.getBlockAt(v.x, v.y, v.z)
                )
            }.toMap()
            launchAsync { config.save(file) }
        } catch (e: Exception) {
            Bukkit.getLogger().warning(e.message)
        }
    }

    fun labelInteractiveRegion(type: InteractiveRegion.Type, label: String, id: Int = -1) {
        when (type) {
            InteractiveRegion.Type.TRIGGER -> labelTrigger(label, id)
            InteractiveRegion.Type.ACTIVE_AREA -> labelActiveArea(label, id)
        }
    }

    fun unmakeInteractiveRegion(type: InteractiveRegion.Type, ieId: Int?) = when (type) {
        InteractiveRegion.Type.TRIGGER -> unmakeTrigger(ieId)
        InteractiveRegion.Type.ACTIVE_AREA -> unmakeActiveArea(ieId)
    }

    fun newInteractiveRegion(type: InteractiveRegion.Type, box: Box) = when (type) {
        InteractiveRegion.Type.TRIGGER -> newTrigger(box)
        InteractiveRegion.Type.ACTIVE_AREA -> newActiveArea(box)
    }

    private fun newActiveArea(box: Box): Int {
        val id = activeAreas.keys.lastOrNull()?.plus(1) ?: 0
        val activeArea = ActiveAreaImpl(id, box.withContainerOriginZero(testOrigin))
        activeAreas = activeAreas.plus(id to activeArea)
        highlightNewInteractiveRegion(activeArea)
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

    private fun newTrigger(box: Box): Int {
        val id = triggers.keys.firstGap()
        val trigger = TriggerImpl(id, box.withContainerOriginZero(testOrigin))
        highlightNewInteractiveRegion(trigger)
        triggers = triggers.plus(id to trigger)
        return id
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
        stopParticleSpammers()
        dungeonBoxBuilder.clear()
        triggerBoxBuilder.clear()
        activeAreaBoxBuilder.clear()
        editor.editableDungeon = null
        if (restoreFormer) {
            DungeonManager.finalDungeons[id]?.isBeingEdited = false
        }
        Bukkit.getPlayer(editor)?.sendFWDMessage(Strings.NO_LONGER_EDITING_DUNGEON)
    }

    fun whatIsMissingForWriteout() = StringBuilder().apply {
        if (!hasTestOrigin) append(Strings.WIM_BOX)
        if (startingLocation == null) append(Strings.WIM_STARTING_LOCATION)
        if (triggers.isEmpty()) append(Strings.WIM_AT_LEAST_ONE_TRIGGER)
        if (activeAreas.isEmpty()) append(Strings.WIM_AT_LEAST_ONE_ACTIVE_AREA)
    }.toString().dropLast(2)

    private fun highlightNewInteractiveRegion(interactiveRegion: InteractiveRegion) {
        ParticleSpammer.highlightBox(interactiveRegion.withContainerOrigin(Vector3i.ZERO, testOrigin).box)
    }

    private fun updateTriggerParticleSpammers(newTriggers: Collection<Trigger>) {
        triggerParticleSpammer?.stop()
        if (!hlFrames) return
        triggerParticleSpammer = getFrameParticleSpammer(Particle.DRIP_LAVA, newTriggers)
    }

    private fun updateActiveAreaParticleSpammers(newActiveAreas: Collection<ActiveArea>) {
        activeAreaParticleSpammer?.stop()
        if (!hlFrames) return
        activeAreaParticleSpammer = getFrameParticleSpammer(Particle.DRIP_WATER, newActiveAreas)
    }

    private fun getFrameParticleSpammer(
        particle: Particle,
        regions: Collection<InteractiveRegion>
    ): ParticleSpammer {
        val frameBlocks = regions.flatMap {
            val box = it.box.origin.withRefSystemOrigin(Vector3i.ZERO, testOrigin)
            it.box.getFrame(box)
        }
        return ParticleSpammer(particle, 1, 500, frameBlocks)
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

    fun onPlayerInteract(event: PlayerInteractEvent) {
        val persistentDataContainer = event.item?.itemMeta?.persistentDataContainer ?: return

        val isTriggerWand = persistentDataContainer
            .get(NamespacedKeys.TRIGGER_TOOL, PersistentDataType.SHORT)
            ?.toShort() == 1.toShort()

        val isActiveAreaWand = !isTriggerWand && persistentDataContainer
            .get(NamespacedKeys.ACTIVE_AREA_TOOL, PersistentDataType.SHORT)
            ?.toShort() == 1.toShort()

        if (!isTriggerWand && !isActiveAreaWand) return
        event.isCancelled = true

        val posNo = when (event.action) {
            Action.LEFT_CLICK_BLOCK -> 1
            Action.RIGHT_CLICK_BLOCK -> 2
            else -> return
        }

        val cmd = "fwde ${if (isTriggerWand) "trigger" else "activearea"} pos$posNo"

        event.player.performCommand(cmd)
    }

    companion object {
        private const val NEW_DUNGEON_TEMP_ID = -69420
    }
}