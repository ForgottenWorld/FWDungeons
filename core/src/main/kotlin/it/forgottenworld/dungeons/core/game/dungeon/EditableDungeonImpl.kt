package it.forgottenworld.dungeons.core.game.dungeon

import com.google.inject.assistedinject.Assisted
import com.google.inject.assistedinject.AssistedInject
import it.forgottenworld.dungeons.api.game.dungeon.Dungeon
import it.forgottenworld.dungeons.api.game.dungeon.DungeonManager
import it.forgottenworld.dungeons.api.game.dungeon.EditableDungeon
import it.forgottenworld.dungeons.api.game.dungeon.FinalDungeon
import it.forgottenworld.dungeons.api.game.dungeon.subelement.chest.Chest
import it.forgottenworld.dungeons.api.game.dungeon.subelement.interactiveregion.ActiveArea
import it.forgottenworld.dungeons.api.game.dungeon.subelement.interactiveregion.InteractiveRegion
import it.forgottenworld.dungeons.api.game.dungeon.subelement.interactiveregion.SpawnArea
import it.forgottenworld.dungeons.api.game.dungeon.subelement.interactiveregion.Trigger
import it.forgottenworld.dungeons.api.math.Box
import it.forgottenworld.dungeons.api.math.Vector3i
import it.forgottenworld.dungeons.api.storage.Storage
import it.forgottenworld.dungeons.api.storage.Storage.Companion.save
import it.forgottenworld.dungeons.api.storage.yaml
import it.forgottenworld.dungeons.core.FWDungeonsPlugin
import it.forgottenworld.dungeons.core.cli.ActiveAreaPaginatedGui
import it.forgottenworld.dungeons.core.cli.ChestsPaginatedGui
import it.forgottenworld.dungeons.core.cli.SpawnAreaPaginatedGui
import it.forgottenworld.dungeons.core.cli.TriggerPaginatedGui
import it.forgottenworld.dungeons.core.game.dungeon.instance.DungeonInstanceFactory
import it.forgottenworld.dungeons.core.game.dungeon.subelement.interactiveregion.activearea.ActiveAreaFactory
import it.forgottenworld.dungeons.core.game.dungeon.subelement.interactiveregion.spawnarea.SpawnAreaFactory
import it.forgottenworld.dungeons.core.game.dungeon.subelement.interactiveregion.trigger.TriggerFactory
import it.forgottenworld.dungeons.core.storage.Configuration
import it.forgottenworld.dungeons.core.storage.Strings
import it.forgottenworld.dungeons.core.utils.*
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.entity.Player
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.persistence.PersistentDataType
import java.io.File
import javax.annotation.Nullable

class EditableDungeonImpl @AssistedInject constructor(
    @Assisted("editor") editor: Player,
    @Assisted("id") override var id: Int = EditableDungeon.NEW_DUNGEON_TEMP_ID,
    @Assisted("name") override var name: String = "NEW DUNGEON",
    @Assisted("description") override var description: String = "",
    @Assisted override var difficulty: Dungeon.Difficulty = Dungeon.Difficulty.MEDIUM,
    @Assisted("minPlayers") override var minPlayers: Int = 1,
    @Assisted("maxPlayers") override var maxPlayers: Int = 2,
    @Nullable @Assisted override var box: Box? = null,
    @Nullable @Assisted override var startingLocation: Vector3i? = null,
    @Assisted("points") override var points: Int = 0,
    @Assisted override var finalInstanceLocations: MutableList<Vector3i> = mutableListOf(),
    @Assisted triggers: Map<Int, Trigger> = mutableMapOf(),
    @Assisted activeAreas: Map<Int, ActiveArea> = mutableMapOf(),
    @Assisted spawnAreas: Map<Int, SpawnArea> = mutableMapOf(),
    @Assisted override val chests: MutableMap<Int, Chest> = mutableMapOf(),
    private val activeAreaFactory: ActiveAreaFactory,
    private val triggerFactory: TriggerFactory,
    private val spawnAreaFactory: SpawnAreaFactory,
    private val plugin: FWDungeonsPlugin,
    private val configuration: Configuration,
    private val namespacedKeys: NamespacedKeys,
    private val dungeonFactory: DungeonFactory,
    private val dungeonInstanceFactory: DungeonInstanceFactory,
    private val dungeonManager: DungeonManager,
    private val storage: Storage
) : EditableDungeon {

    @AssistedInject
    constructor(
        @Assisted("editor") editor: Player,
        @Assisted dungeon: Dungeon,
        activeAreaFactory: ActiveAreaFactory,
        triggerFactory: TriggerFactory,
        spawnAreaFactory: SpawnAreaFactory,
        plugin: FWDungeonsPlugin,
        configuration: Configuration,
        namespacedKeys: NamespacedKeys,
        dungeonFactory: DungeonFactory,
        dungeonInstanceFactory: DungeonInstanceFactory,
        dungeonManager: DungeonManager,
        storage: Storage
    ) : this(
        editor,
        dungeon.id,
        dungeon.name,
        dungeon.description,
        dungeon.difficulty,
        dungeon.minPlayers,
        dungeon.maxPlayers,
        dungeon.box!!.copy(),
        dungeon.startingLocation!!.copy(),
        dungeon.points,
        dungeonManager
            .getDungeonInstances(dungeon)
            .values
            .map { it.origin }
            .toMutableList(),
        dungeon.triggers,
        dungeon.activeAreas,
        dungeon.spawnAreas,
        dungeon.chests.toMutableMap(),
        activeAreaFactory,
        triggerFactory,
        spawnAreaFactory,
        plugin,
        configuration,
        namespacedKeys,
        dungeonFactory,
        dungeonInstanceFactory,
        dungeonManager,
        storage
    )

    private val editor = editor.uniqueId

    override lateinit var testOrigin: Vector3i

    private var editedFromExisting = id != EditableDungeon.NEW_DUNGEON_TEMP_ID

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

    override var spawnAreas = spawnAreas
        private set(value) {
            field = value
            updateSpawnAreaParticleSpammers(value.values)
        }

    override val dungeonBoxBuilder = Box.Builder()
    override val triggerBoxBuilder = Box.Builder()
    override val activeAreaBoxBuilder = Box.Builder()
    override val spawnAreaBoxBuilder = Box.Builder()

    override val hasTestOrigin get() = this::testOrigin.isInitialized

    private var hlFrames = false
    private var triggerParticleSpammer: ParticleSpammer? = null
    private var activeAreaParticleSpammer: ParticleSpammer? = null
    private var spawnAreaParticleSpammer: ParticleSpammer? = null

    private val triggersGui = TriggerPaginatedGui(this)
    private val activeAreasGui = ActiveAreaPaginatedGui(this)
    private val spawnAreasGui = SpawnAreaPaginatedGui(this)
    private val chestsGui = ChestsPaginatedGui(this)

    override fun finalize(): FinalDungeon {
        if (id == EditableDungeon.NEW_DUNGEON_TEMP_ID) {
            id = dungeonManager.getFirstAvailableFinalDungeonId()
        }
        val finalDungeon = dungeonFactory.createFinal(this)
        if (!editedFromExisting) finalDungeon.isActive = false
        dungeonManager.registerFinalDungeon(finalDungeon)
        saveToConfigAndCreateInstances(id, finalDungeon)
        onDestroy()
        return finalDungeon
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    private fun saveToConfigAndCreateInstances(newId: Int, finalDungeon: FinalDungeon) {
        try {
            yaml {
                val file = File(plugin.dataFolder, "instances.yml")
                if (file.exists()) load(file)
                val dgConf = createSection("$newId")
                val instances = finalInstanceLocations.withIndex().associate { (k, v) ->
                    val section = dgConf.createSection("$k")
                    val inst = dungeonInstanceFactory.create(finalDungeon, v)
                    storage.save(inst, section)
                    k to inst
                }
                dungeonManager.setDungeonInstances(finalDungeon, instances)
                launchAsync { save(file) }
            }
        } catch (e: Exception) {
            sendConsoleMessage(e.message ?: e.toString())
        }
    }

    override fun labelInteractiveRegion(type: InteractiveRegion.Type, label: String, id: Int) {
        when (type) {
            InteractiveRegion.Type.TRIGGER -> labelTrigger(label, id)
            InteractiveRegion.Type.ACTIVE_AREA -> labelActiveArea(label, id)
            InteractiveRegion.Type.SPAWN_AREA -> labelSpawnArea(label, id)
        }
    }

    override fun showChestsGuiToPlayer(player: Player, page: Int) {
        player.sendMessage(chestsGui.get(page))
    }

    override fun showInteractiveRegionGuiToPlayer(
        player: Player,
        type: InteractiveRegion.Type,
        page: Int
    ) {
        player.sendMessage(
            when (type) {
                InteractiveRegion.Type.TRIGGER -> triggersGui.get(page)
                InteractiveRegion.Type.ACTIVE_AREA -> activeAreasGui.get(page)
                InteractiveRegion.Type.SPAWN_AREA -> spawnAreasGui.get(page)
            }
        )
    }

    override fun unmakeInteractiveRegion(type: InteractiveRegion.Type, ieId: Int?) = when (type) {
        InteractiveRegion.Type.TRIGGER -> unmakeTrigger(ieId)
        InteractiveRegion.Type.ACTIVE_AREA -> unmakeActiveArea(ieId)
        InteractiveRegion.Type.SPAWN_AREA -> unmakeSpawnArea(ieId)
    }

    override fun newInteractiveRegion(type: InteractiveRegion.Type, box: Box) = when (type) {
        InteractiveRegion.Type.TRIGGER -> newTrigger(box)
        InteractiveRegion.Type.ACTIVE_AREA -> newActiveArea(box)
        InteractiveRegion.Type.SPAWN_AREA -> newSpawnArea(box)
    }

    private fun newActiveArea(box: Box): Int {
        val id = activeAreas.keys.lastOrNull()?.plus(1) ?: 0
        val volume = box.width * box.height * box.depth
        val materialCounts = mutableMapOf<Material, Int>()

        var prevalentMaterial = Material.AIR
        var prevalentCount = 0
        var runnerUpCount = 0

        val blockIterator = box.getBlockIterator(configuration.dungeonWorld)
        for ((i,block) in blockIterator.withIndex()) {
            val count = (materialCounts[block.type] ?: 0) + 1
            materialCounts[block.type] = count
            if (count > prevalentCount) {
                if (prevalentMaterial != block.type) {
                    runnerUpCount = prevalentCount
                    prevalentMaterial = block.type
                }
                prevalentCount = count
            } else if (count > runnerUpCount) {
                runnerUpCount = count
            }
            if (volume - i - 1 < prevalentCount - runnerUpCount) break
        }

        val activeArea = activeAreaFactory.create(
            id,
            box.withContainerOriginZero(testOrigin),
            prevalentMaterial,
            null
        )

        Bukkit.getPlayer(editor)?.sendPrefixedMessage(
            Strings.PREVALENT_MATERIAL_IN_REGION_IS,
            prevalentMaterial.name
        )

        activeAreas = activeAreas.plus(id to activeArea)
        highlightNewInteractiveRegion(activeArea)
        return id
    }

    private fun unmakeActiveArea(aaId: Int?) =
        if (aaId == null) {
            activeAreas.keys.last().also { activeAreas = activeAreas.minus(it) }
        } else {
            activeAreas = activeAreas - aaId
            aaId
        }

    private fun labelActiveArea(label: String, id: Int = -1) {
        if (id == -1) {
            activeAreas.values.lastOrNull()?.label = label
        } else {
            activeAreas[id]?.label = label
        }
    }

    private fun newSpawnArea(box: Box): Int {
        val id = spawnAreas.keys.firstGap()
        val world = configuration.dungeonWorld
        val heightMap = Array(box.width) { x ->
            IntArray(box.depth) { z ->
                for (y in 0..box.height) {
                    val (oX,oY,oZ) = box.origin + Vector3i(x,y,z)

                    val firstFree = !world
                        .getBlockAt(oX, oY, oZ)
                        .boundingBox
                        .contains(oX + 0.5, oY + 0.5, oZ + 0.5)

                    val secondFree = firstFree && !world
                        .getBlockAt(oX, oY + 1, oZ)
                        .boundingBox
                        .contains(oX + 0.5, oY + 1.5, oZ + 0.5)

                    if (secondFree) return@IntArray y
                }
                -1
            }
        }

        val spawnArea = spawnAreaFactory.create(
            id,
            box.withContainerOriginZero(testOrigin),
            heightMap
        )

        highlightNewInteractiveRegion(spawnArea)
        spawnAreas = spawnAreas.plus(id to spawnArea)
        return id
    }

    private fun unmakeSpawnArea(saId: Int?) =
        if (saId == null) {
            spawnAreas.keys.last().also { spawnAreas = spawnAreas.minus(it) }
        } else {
            spawnAreas = spawnAreas - saId
            saId
        }

    private fun labelSpawnArea(label: String, id: Int) {
        if (id == -1) {
            spawnAreas.values.lastOrNull()?.label = label
        } else {
            spawnAreas[id]?.label = label
        }
    }

    private fun newTrigger(box: Box): Int {
        val id = triggers.keys.firstGap()
        val trigger = triggerFactory.create(id, box.withContainerOriginZero(testOrigin))
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

    override fun setupTestBox() {
        testOrigin = finalInstanceLocations.first()
    }

    override fun onDestroy(restoreFormer: Boolean) {
        stopParticleSpammers()
        dungeonBoxBuilder.clear()
        triggerBoxBuilder.clear()
        activeAreaBoxBuilder.clear()
        dungeonManager.setPlayerEditableDungeon(editor, null)
        if (restoreFormer) {
            dungeonManager.getFinalDungeonById(id)?.run {
                isBeingEdited = false
                val instances = finalInstanceLocations.withIndex().associate { (k, v) ->
                    k to dungeonInstanceFactory.create(this, v)
                }
                dungeonManager.setDungeonInstances(this, instances)
            }
        }
        Bukkit.getPlayer(editor)?.sendPrefixedMessage(Strings.NO_LONGER_EDITING_DUNGEON)
    }

    override fun whatIsMissingForWriteout() = StringBuilder().apply {
        if (!hasTestOrigin) append(Strings.WIM_BOX)
        if (startingLocation == null) append(Strings.WIM_STARTING_LOCATION)
        if (triggers.isEmpty()) append(Strings.WIM_AT_LEAST_ONE_TRIGGER)
        if (activeAreas.isEmpty()) append(Strings.WIM_AT_LEAST_ONE_ACTIVE_AREA)
        if (spawnAreas.isEmpty()) append(Strings.WIM_AT_LEAST_ONE_SPAWN_AREA)
    }.toString().dropLast(2)

    private fun highlightNewInteractiveRegion(interactiveRegion: InteractiveRegion) {
        val locs = interactiveRegion.box
            .withContainerOrigin(Vector3i.ZERO, testOrigin)
            .getCenterOfAllBlocks()

        ParticleSpammer.builder().particle(Particle.COMPOSTER)
            .locations(locs)
            .world(configuration.dungeonWorld)
            .oneShot(20)
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

    private fun updateSpawnAreaParticleSpammers(newSpawnAreas: Collection<SpawnArea>) {
        spawnAreaParticleSpammer?.stop()
        if (!hlFrames) return
        spawnAreaParticleSpammer = getFrameParticleSpammer(Particle.DRIPPING_OBSIDIAN_TEAR, newSpawnAreas)
    }

    private fun getFrameParticleSpammer(
        particle: Particle,
        regions: Collection<InteractiveRegion>
    ): ParticleSpammer {

        val frameBlocks = regions.flatMap {
            val box = it.box.origin.translatePlane(Vector3i.ZERO, testOrigin)
            it.box.getFrame(box)
        }

        return ParticleSpammer.builder()
            .particle(particle)
            .locations(frameBlocks)
            .world(configuration.dungeonWorld)
            .build()
    }

    private fun updateParticleSpammers() {
        if (!hlFrames) return
        updateTriggerParticleSpammers(triggers.values)
        updateActiveAreaParticleSpammers(activeAreas.values)
        updateSpawnAreaParticleSpammers(spawnAreas.values)
    }

    private fun stopParticleSpammers() {
        triggerParticleSpammer?.stop()
        triggerParticleSpammer = null
        activeAreaParticleSpammer?.stop()
        activeAreaParticleSpammer = null
        spawnAreaParticleSpammer?.stop()
        spawnAreaParticleSpammer = null
    }

    override fun toggleEditorHighlights() {
        hlFrames = !hlFrames
        if (hlFrames) {
            updateParticleSpammers()
        } else {
            stopParticleSpammers()
        }
    }

    override fun onPlayerInteract(event: PlayerInteractEvent) {
        val posNo = when (event.action) {
            Action.LEFT_CLICK_BLOCK -> 1
            Action.RIGHT_CLICK_BLOCK -> 2
            else -> return
        }

        val persistentDataContainer = event.item?.itemMeta?.persistentDataContainer ?: return

        val isTriggerWand = persistentDataContainer
            .get(namespacedKeys.triggerTool, PersistentDataType.SHORT)
            ?.toShort() == 1.toShort()

        val isActiveAreaWand = !isTriggerWand && persistentDataContainer
            .get(namespacedKeys.activeAreaTool, PersistentDataType.SHORT)
            ?.toShort() == 1.toShort()

        val isSpawnAreaWand = !isActiveAreaWand && persistentDataContainer
            .get(namespacedKeys.spawnAreaTool, PersistentDataType.SHORT)
            ?.toShort() == 1.toShort()

        if (!isTriggerWand && !isActiveAreaWand && !isSpawnAreaWand) return
        event.isCancelled = true

        val cmd = "fwde ${
            when {
                isTriggerWand -> "trigger"
                isActiveAreaWand -> "activearea"
                isSpawnAreaWand -> "spawnarea"
                else -> return
            }
        } pos$posNo"

        event.player.performCommand(cmd)
    }

}