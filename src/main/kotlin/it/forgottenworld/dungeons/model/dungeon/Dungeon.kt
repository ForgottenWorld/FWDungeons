package it.forgottenworld.dungeons.model.dungeon

import it.forgottenworld.dungeons.model.activearea.ActiveArea
import it.forgottenworld.dungeons.model.box.Box
import it.forgottenworld.dungeons.model.trigger.Trigger
import org.bukkit.util.BlockVector

class Dungeon(val id: Int) {
    var name = ""
    lateinit var box: Box
    lateinit var startingLocation: BlockVector
    var triggers = mutableListOf<Trigger>()
    var activeAreas = mutableListOf<ActiveArea>()
    var instances = mutableListOf<DungeonInstance>()

    fun hasBox() = ::box.isInitialized

    constructor(id: Int,
                name: String,
                box: Box,
                startingLocation: BlockVector,
                triggers: MutableList<Trigger>,
                activeAreas: MutableList<ActiveArea>,
                instances: MutableList<DungeonInstance>) : this(id) {
        this.name = name
        this.box = box
        this.startingLocation = startingLocation
        this.triggers = triggers
        this.activeAreas = activeAreas
        this.instances = instances
    }

    fun whatIsMissingForWriteout(): String {
        var res = ""
        if (name == "") res += "name, "
        if (!::box.isInitialized) res += "box, "
        if (!::startingLocation.isInitialized) res += "starting location, "
        if (triggers.isEmpty()) res += "at least one trigger, "
        if (activeAreas.isEmpty()) res += "at least one active area, "
        return res.dropLast(2)
    }

    fun getActiveAreaById(id: Int): ActiveArea? =
            activeAreas.find { it.id == id }

    fun getTriggerById(id: Int): Trigger? =
            triggers.find { it.id == id }
}