package it.forgottenworld.dungeons.model.dungeon

import it.forgottenworld.dungeons.config.ConfigManager
import it.forgottenworld.dungeons.config.Strings
import it.forgottenworld.dungeons.model.box.Box
import it.forgottenworld.dungeons.model.box.BoxBuilder
import it.forgottenworld.dungeons.model.instance.DungeonTestInstance
import it.forgottenworld.dungeons.model.interactiveelement.ActiveArea
import it.forgottenworld.dungeons.model.interactiveelement.InteractiveElementType
import it.forgottenworld.dungeons.model.interactiveelement.Trigger
import it.forgottenworld.dungeons.utils.ktx.*
import it.forgottenworld.dungeons.utils.safePlayer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.bukkit.Bukkit
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.util.BlockVector
import java.io.File
import java.util.*
import kotlin.properties.Delegates

class EditableDungeon(editor: Player) : Dungeon {

    override var id: Int = -1000
    override var name = "NEW DUNGEON"
    override var description = ""
    override var difficulty = Difficulty.MEDIUM
    override var numberOfPlayers = 1..2
    override var box: Box? = null
    override var startingLocation: BlockVector? = null
    override var triggers by Delegates.observable(mapOf<Int, Trigger>()) { _, _, newValue -> testInstance?.updateTriggers(newValue) }
    override var activeAreas by Delegates.observable(mapOf<Int, ActiveArea>()) { _, _, newValue -> testInstance?.updateActiveAreas(newValue) }
    override var points = 0

    private val editor by safePlayer(editor)
    val dungeonBoxBuilder = BoxBuilder()
    val triggerBoxBuilder = BoxBuilder()
    val activeAreaBoxBuilder = BoxBuilder()
    var testInstance: DungeonTestInstance? = null
    var finalInstanceLocations = mutableListOf<BlockVector>()

    val hasTestInstance get() = testInstance != null

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
            startingLocation!!.clone(),
            triggers.toMap(),
            activeAreas.toMap(),
            mapOf()
        )

        FinalDungeon.dungeons[newId] = finalDungeon

        try {
            val config = YamlConfiguration()
            val file = File(plugin.dataFolder, "instances.yml")
            if (file.exists()) config.load(file)
            val dgConf = config.createSection("$newId")
            finalDungeon.instances = finalInstanceLocations.withIndex().map { (k,v) ->
                dgConf.createSection("$k").run {
                    set("x", v.blockX)
                    set("y", v.blockY)
                    set("z", v.blockZ)
                }
                k to finalDungeon.createInstance(
                        ConfigManager.dungeonWorld.getBlockAt(v.blockX, v.blockY, v.blockZ))
            }.toMap()
            @Suppress("BlockingMethodInNonBlockingContext")
            launchAsync { config.save(file) }
        } catch (e: Exception) {
            Bukkit.getLogger().warning(e.message)
        }

        onDestroy()
        return finalDungeon
    }
    
    fun labelInteractiveElement(type: InteractiveElementType, label: String) {
        if (type == InteractiveElementType.TRIGGER) labelTrigger(label) else labelActiveArea(label)
    }

    fun unmakeInteractiveElement(type: InteractiveElementType, ieId: Int?): Int {
        return if (type == InteractiveElementType.TRIGGER) unmakeTrigger(ieId) else unmakeActiveArea(ieId)
    }

    suspend fun newInteractiveElement(type: InteractiveElementType, box: Box) =
            if (type == InteractiveElementType.TRIGGER) newTrigger(box) else newActiveArea(box)

    private fun newActiveArea(box: Box): Int {
        val id = activeAreas.keys.lastOrNull()?.plus(1) ?: 0
        ActiveArea(id, box.withContainerOrigin(testInstance!!.origin,BlockVector(0, 0, 0))).let {
            activeAreas = activeAreas.plus(id to it)
            testInstance?.highlightNewInteractiveElement(it)
        }
        return id
    }

    private fun unmakeActiveArea(aaId: Int?) =
            if (aaId == null) activeAreas.keys.last().also { activeAreas = activeAreas.minus(it) }
            else {
                activeAreas = activeAreas.minus(aaId)
                aaId
            }

    private fun labelActiveArea(label: String) {
        activeAreas.values.lastOrNull()?.label = label
        val inst = testInstance ?: return
        inst.triggers.values.lastOrNull()?.label = label
    }

    private suspend fun newTrigger(box: Box): Int {
        val id = triggers.keys.lastOrNull()?.plus(1) ?: 0

        return withContext(Dispatchers.minecraft) {
            testInstance?.stopCheckingTriggers()
            Trigger(id, box.withContainerOrigin(testInstance!!.origin, BlockVector(0, 0, 0))).let {
                testInstance?.highlightNewInteractiveElement(it)
                triggers = triggers.plus(id to it)
            }
            testInstance?.startCheckingTriggers()
            id
        }
    }

    private fun unmakeTrigger(tId: Int?) =
            if (tId == null) triggers.keys.last().also { triggers = triggers.minus(it) }
            else {
                triggers = triggers.minus(tId)
                tId
            }

    private fun labelTrigger(label: String) {
        triggers.values.lastOrNull()?.label = label
        val inst = testInstance ?: return
        inst.triggers.values.lastOrNull()?.label = label
    }

    fun createTestInstance(creator: Player) {
        testInstance = DungeonTestInstance(this, finalInstanceLocations.first(), creator.uniqueId)
    }

    fun onDestroy(restoreFormer: Boolean = false) {
        val player = editor ?: return
        testInstance?.onDestroy()
        testInstance = null
        dungeonBoxBuilder.clear()
        triggerBoxBuilder.clear()
        activeAreaBoxBuilder.clear()
        player.editableDungeon = null
        if (restoreFormer) FinalDungeon.dungeons[id]?.isBeingEdited = false
        player.sendFWDMessage(Strings.NO_LONGER_EDITING_DUNGEON)
    }

    fun whatIsMissingForWriteout() = StringBuilder().apply {
        if (!hasTestInstance) append(Strings.WIM_BOX)
        if (startingLocation == null) append(Strings.WIM_STARTING_LOCATION)
        if (triggers.isEmpty()) append(Strings.WIM_AT_LEAST_ONE_TRIGGER)
        if (activeAreas.isEmpty()) append(Strings.WIM_AT_LEAST_ONE_ACTIVE_AREA)
    }.toString().dropLast(2)

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