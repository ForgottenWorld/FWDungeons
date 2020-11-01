package it.forgottenworld.dungeons.model.dungeon

import it.forgottenworld.dungeons.FWDungeonsPlugin
import it.forgottenworld.dungeons.config.ConfigManager
import it.forgottenworld.dungeons.model.box.Box
import it.forgottenworld.dungeons.model.box.BoxBuilder
import it.forgottenworld.dungeons.model.instance.DungeonTestInstance
import it.forgottenworld.dungeons.model.interactiveelement.ActiveArea
import it.forgottenworld.dungeons.model.interactiveelement.InteractiveElementType
import it.forgottenworld.dungeons.model.interactiveelement.Trigger
import it.forgottenworld.dungeons.utils.ktx.firstMissing
import it.forgottenworld.dungeons.utils.ktx.safePlayer
import it.forgottenworld.dungeons.utils.ktx.sendFWDMessage
import it.forgottenworld.dungeons.utils.launchAsync
import it.forgottenworld.dungeons.utils.minecraft
import it.forgottenworld.dungeons.utils.observableMapOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.md_5.bungee.api.ChatColor
import org.bukkit.Bukkit
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.util.BlockVector
import java.io.File
import java.util.*

class EditableDungeon(editor: Player) : Dungeon {

    override val id: Int = -1000
    override var name = "NEW DUNGEON"
    override var description = ""
    override var difficulty = Difficulty.MEDIUM
    override var numberOfPlayers = IntRange(1, 2)
    override var box: Box? = null
    override var startingLocation: BlockVector? = null
    override var triggers = observableMapOf<Int, Trigger>()
    override var activeAreas = observableMapOf<Int, ActiveArea>()
    override var points = 0

    private val editor by safePlayer(editor)
    val dungeonBoxBuilder = BoxBuilder()
    val triggerBoxBuilder = BoxBuilder()
    val activeAreaBoxBuilder = BoxBuilder()
    var testInstance: DungeonTestInstance? = null
    var finalInstanceLocations = mutableListOf<BlockVector>()

    val hasTestInstance get() = testInstance != null

    fun finalize(): FinalDungeon {
        val newId = FinalDungeon.dungeons.keys.firstMissing()

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
            val file = File(FWDungeonsPlugin.pluginDataFolder, "instances.yml")
            if (file.exists()) config.load(file)
            val dgconf = config.createSection("$newId")
            finalDungeon.instances = finalInstanceLocations.withIndex().map { (k,v) ->
                dgconf.createSection("$k").run {
                    set("x", v.blockX)
                    set("y", v.blockY)
                    set("z", v.blockZ)
                }
                k to finalDungeon.createInstance(
                        ConfigManager.dungeonWorld.getBlockAt(v.blockX, v.blockY, v.blockZ))
            }.toMap()
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

    fun unmakeInteractiveElement(type: InteractiveElementType): Int {
        return if (type == InteractiveElementType.TRIGGER) unmakeTrigger() else unmakeActiveArea()
    }

    suspend fun newInteractiveElement(type: InteractiveElementType, box: Box) =
            if (type == InteractiveElementType.TRIGGER) newTrigger(box) else newActiveArea(box)

    private fun newActiveArea(box: Box): Int {
        val id = activeAreas.keys.lastOrNull()?.plus(1) ?: 0
        activeAreas[id] = ActiveArea(id, box.withContainerOrigin(testInstance!!.origin,BlockVector(0, 0, 0)))
        return id
    }

    private fun unmakeActiveArea() =
            activeAreas.keys.lastOrNull()?.also { activeAreas.remove(it) } ?: -1

    private fun labelActiveArea(label: String) {
        activeAreas.values.lastOrNull()?.label = label
        val inst = testInstance ?: return
        inst.triggers.values.lastOrNull()?.label = label
    }

    private suspend fun newTrigger(box: Box): Int {
        val id = triggers.keys.lastOrNull()?.plus(1) ?: 0
        return withContext(Dispatchers.minecraft) {
            testInstance?.stopCheckingTriggersAndWait()
            triggers[id] = Trigger(id, box.withContainerOrigin(testInstance!!.origin, BlockVector(0, 0, 0)))
            testInstance?.startCheckingTriggers()
            id
        }
    }

    private fun unmakeTrigger() =
            triggers.keys.lastOrNull()?.also { triggers.remove(it) } ?: -1

    private fun labelTrigger(label: String) {
        triggers.values.lastOrNull()?.label = label
        val inst = testInstance ?: return
        inst.triggers.values.lastOrNull()?.label = label
    }

    fun createTestInstance(creator: Player) {
        testInstance = DungeonTestInstance(this, finalInstanceLocations.first(), creator.uniqueId)
    }

    fun onDestroy() {
        val player = editor ?: return
        triggers.clearObservers()
        activeAreas.clearObservers()
        testInstance?.onDestroy()
        testInstance = null
        dungeonBoxBuilder.clear()
        triggerBoxBuilder.clear()
        activeAreaBoxBuilder.clear()
        player.editableDungeon = null
        player.sendFWDMessage("${ChatColor.GRAY}You're no longer editing a dungeon")
    }

    fun whatIsMissingForWriteout() = StringBuilder().apply {
        if (!hasTestInstance) append("box, ")
        if (startingLocation == null) append("starting location, ")
        if (triggers.isEmpty()) append("at least one trigger, ")
        if (activeAreas.isEmpty()) append("at least one active area, ")
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