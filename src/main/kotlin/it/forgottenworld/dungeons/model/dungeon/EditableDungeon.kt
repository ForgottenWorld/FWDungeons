package it.forgottenworld.dungeons.model.dungeon

import it.forgottenworld.dungeons.config.ConfigManager
import it.forgottenworld.dungeons.manager.DungeonManager
import it.forgottenworld.dungeons.model.box.Box
import it.forgottenworld.dungeons.model.instance.DungeonTestInstance
import it.forgottenworld.dungeons.model.interactiveelement.ActiveArea
import it.forgottenworld.dungeons.model.interactiveelement.InteractiveElementType
import it.forgottenworld.dungeons.model.interactiveelement.Trigger
import it.forgottenworld.dungeons.utils.firstMissing
import org.bukkit.entity.Player
import org.bukkit.util.BlockVector

class EditableDungeon(val id: Int) : Dungeon {

    var name = "NEW DUNGEON"
    var description = ""
    var difficulty = Difficulty.MEDIUM
    var numberOfPlayers = IntRange(1, 2)
    var box: Box? = null
    var startingLocation: BlockVector? = null
    var triggers = mutableMapOf<Int, Trigger>()
    var activeAreas = mutableMapOf<Int, ActiveArea>()
    var points = 0

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

        testInstance?.onDestroy()
        testInstance = null
        DungeonManager.dungeons[newId] = finalDungeon
        return finalDungeon
    }
    
    fun labelInteractiveElement(type: InteractiveElementType, label: String) {
        if (type == InteractiveElementType.TRIGGER) labelTrigger(label) else labelActiveArea(label)
    }

    fun unmakeInteractiveElement(type: InteractiveElementType): Int {
        return if (type == InteractiveElementType.TRIGGER) unmakeTrigger() else unmakeActiveArea()
    }

    fun newInteractiveElement(type: InteractiveElementType, box: Box): Int {
        val inst = testInstance ?: return -1
        val id = if (type == InteractiveElementType.TRIGGER) newTrigger(box) else newActiveArea(box)
        inst.newInteractiveElement(type, id, box)
        return id
    }

    private fun newActiveArea(box: Box): Int {
        val id = activeAreas.keys.lastOrNull()?.plus(1) ?: 0
        activeAreas[id] = ActiveArea(id, box.withContainerOrigin(testInstance!!.origin,BlockVector(0, 0, 0)))
        return id
    }

    private fun unmakeActiveArea(): Int {
        activeAreas.keys.lastOrNull()?.let { activeAreas.remove(it) }
        val inst = testInstance ?: return -1
        return inst.activeAreas.keys.last().also { inst.activeAreas.remove(it) }
    }

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

    private fun unmakeTrigger(): Int {
        triggers.keys.lastOrNull()?.let { triggers.remove(it) }
        val inst = testInstance ?: return -1
        return inst.triggers.keys.last().also { inst.triggers.remove(it) }
    }

    private fun labelTrigger(label: String) {
        triggers.values.lastOrNull()?.label = label
        val inst = testInstance ?: return
        inst.triggers.values.lastOrNull()?.label = label
    }

    fun createTestInstance(at: BlockVector, creator: Player) {
        val triggs = triggers
                .map { (k,v) -> k to v.withContainerOrigin(BlockVector(0, 0, 0), at)}
                .toMap()
                .toMutableMap()

        val aas = activeAreas.map { (k,v) -> k to v.withContainerOrigin(BlockVector(0, 0, 0), at)}
                .toMap()
                .toMutableMap()

        testInstance = DungeonTestInstance(-1, this, at, triggs, aas, creator)
    }

    fun whatIsMissingForWriteout() = StringBuilder().apply {
        if (!hasTestInstance) append("box, ")
        if (startingLocation == null) append("starting location, ")
        if (triggers.isEmpty()) append("at least one trigger, ")
        if (activeAreas.isEmpty()) append("at least one active area, ")
    }.toString().dropLast(2)
}