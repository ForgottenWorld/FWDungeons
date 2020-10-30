package it.forgottenworld.dungeons.model.dungeon

import it.forgottenworld.dungeons.config.ConfigManager
import it.forgottenworld.dungeons.manager.DungeonManager
import it.forgottenworld.dungeons.model.box.Box
import it.forgottenworld.dungeons.model.instance.DungeonTestInstance
import it.forgottenworld.dungeons.model.interactiveelement.ActiveArea
import it.forgottenworld.dungeons.model.interactiveelement.InteractiveElementType
import it.forgottenworld.dungeons.model.interactiveelement.Trigger
import it.forgottenworld.dungeons.utils.ktx.firstMissing
import it.forgottenworld.dungeons.utils.observableMapOf
import org.bukkit.entity.Player
import org.bukkit.util.BlockVector

class EditableDungeon(override val id: Int) : Dungeon {

    override var name = "NEW DUNGEON"
    override var description = ""
    override var difficulty = Difficulty.MEDIUM
    override var numberOfPlayers = IntRange(1, 2)
    override var box: Box? = null
    override var startingLocation: BlockVector? = null
    override var triggers = observableMapOf<Int, Trigger>()
    override var activeAreas = observableMapOf<Int, ActiveArea>()
    override var points = 0

    var testInstance: DungeonTestInstance? = null
    var finalInstanceLocations = mutableMapOf<Int, BlockVector>()

    val hasTestInstance get() = testInstance != null
    
    fun finalize(): FinalDungeon {
        val newId = DungeonManager.dungeons.keys.firstMissing()

        val finalDungeon = FinalDungeon(
            newId,
            name,
            description,
            difficulty,
            points,
            numberOfPlayers,
            box!!,
            startingLocation!!,
            triggers,
            activeAreas,
            mapOf()
        )

        finalDungeon.instances = finalInstanceLocations.map { (k,v) ->
            k to finalDungeon.createInstance(
                    ConfigManager.dungeonWorld.getBlockAt(v.blockX, v.blockY, v.blockZ),
                    k)
        }.toMap()

        onDestroy()
        DungeonManager.dungeons[newId] = finalDungeon
        return finalDungeon
    }
    
    fun labelInteractiveElement(type: InteractiveElementType, label: String) {
        if (type == InteractiveElementType.TRIGGER) labelTrigger(label) else labelActiveArea(label)
    }

    fun unmakeInteractiveElement(type: InteractiveElementType): Int {
        return if (type == InteractiveElementType.TRIGGER) unmakeTrigger() else unmakeActiveArea()
    }

    fun newInteractiveElement(type: InteractiveElementType, box: Box) =
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

    private fun newTrigger(box: Box): Int {
        val id = triggers.keys.lastOrNull()?.plus(1) ?: 0
        triggers[id] = Trigger(id, box.withContainerOrigin(testInstance!!.origin,BlockVector(0, 0, 0)))
        return id
    }

    private fun unmakeTrigger() =
            triggers.keys.lastOrNull()?.also { triggers.remove(it) } ?: -1

    private fun labelTrigger(label: String) {
        triggers.values.lastOrNull()?.label = label
        val inst = testInstance ?: return
        inst.triggers.values.lastOrNull()?.label = label
    }

    fun createTestInstance(at: BlockVector, creator: Player) {
        testInstance = DungeonTestInstance(-1, this, at, creator.uniqueId)
    }

    fun onDestroy() {
        triggers.clearObservers()
        activeAreas.clearObservers()
        testInstance?.onDestroy()
        testInstance = null
    }

    fun whatIsMissingForWriteout() = StringBuilder().apply {
        if (!hasTestInstance) append("box, ")
        if (startingLocation == null) append("starting location, ")
        if (triggers.isEmpty()) append("at least one trigger, ")
        if (activeAreas.isEmpty()) append("at least one active area, ")
    }.toString().dropLast(2)
}