package it.forgottenworld.dungeons.game.dungeon

import it.forgottenworld.dungeons.config.ConfigManager
import it.forgottenworld.dungeons.config.Strings
import it.forgottenworld.dungeons.game.box.Box
import it.forgottenworld.dungeons.game.chest.Chest
import it.forgottenworld.dungeons.game.instance.DungeonTestInstance
import it.forgottenworld.dungeons.game.interactiveregion.ActiveArea
import it.forgottenworld.dungeons.game.interactiveregion.InteractiveRegion
import it.forgottenworld.dungeons.game.interactiveregion.Trigger
import it.forgottenworld.dungeons.utils.Vector3i
import it.forgottenworld.dungeons.utils.firstMissing
import it.forgottenworld.dungeons.utils.launchAsync
import it.forgottenworld.dungeons.utils.minecraft
import it.forgottenworld.dungeons.utils.player
import it.forgottenworld.dungeons.utils.plugin
import it.forgottenworld.dungeons.utils.sendFWDMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.bukkit.Bukkit
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import java.io.File
import java.util.UUID
import kotlin.properties.Delegates

class EditableDungeon(editor: Player) : Dungeon {

    override var id = -1000
    override var name = "NEW DUNGEON"
    override var description = ""
    override var difficulty = Dungeon.Difficulty.MEDIUM
    override var numberOfPlayers = 1..2
    override var box: Box? = null
    override var startingLocation: Vector3i? = null
    override var points = 0

    override var triggers by Delegates.observable(mapOf<Int, Trigger>()) { _, _, newValue ->
        testInstance?.updateTriggers(newValue)
    }

    override var activeAreas by Delegates.observable(mapOf<Int, ActiveArea>()) { _, _, newValue ->
        testInstance?.updateActiveAreas(newValue)
    }

    override var chests = mapOf<Int, Chest>()

    private val editor by player(editor)
    val dungeonBoxBuilder = Box.Builder()
    val triggerBoxBuilder = Box.Builder()
    val activeAreaBoxBuilder = Box.Builder()
    var testInstance: DungeonTestInstance? = null
    var finalInstanceLocations = mutableListOf<Vector3i>()

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
        ActiveArea(
            id,
            box.withContainerOrigin(
                testInstance!!.origin,
                Vector3i(0, 0, 0)
            )
        ).let {
            activeAreas = activeAreas.plus(id to it)
            testInstance?.highlightNewInteractiveRegion(it)
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
            testInstance?.activeAreas?.values?.lastOrNull()?.label = label
        } else {
            activeAreas[id]?.label = label
            testInstance?.activeAreas?.get(id)?.label = label
        }
    }

    private suspend fun newTrigger(box: Box): Int {
        val id = triggers.keys.lastOrNull()?.plus(1) ?: 0

        return withContext(Dispatchers.minecraft) {
            Trigger(
                id,
                box.withContainerOrigin(
                    testInstance!!.origin,
                    Vector3i(0, 0, 0)
                )
            ).let {
                testInstance?.highlightNewInteractiveRegion(it)
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
            testInstance?.triggers?.values?.lastOrNull()?.label = label
        } else {
            triggers[id]?.label = label
            testInstance?.triggers?.get(id)?.label = label
        }
    }

    fun createTestInstance() {
        testInstance = DungeonTestInstance(this, finalInstanceLocations.first())
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