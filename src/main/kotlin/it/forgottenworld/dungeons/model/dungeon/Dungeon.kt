package it.forgottenworld.dungeons.model.dungeon

import it.forgottenworld.dungeons.model.box.Box
import it.forgottenworld.dungeons.model.trigger.Trigger

class Dungeon(val id: Int) {
    var name = ""
    lateinit var box: Box
    var triggers = mutableListOf<Trigger>()
    var instances = mutableListOf<DungeonInstance>()

    fun hasBox() = ::box.isInitialized

    constructor(id: Int,
                name: String,
                box: Box,
                triggers: MutableList<Trigger>,
                instances: MutableList<DungeonInstance>) : this(id) {
        this.name = name
        this.box = box
        this.triggers = triggers
        this.instances = instances
    }
}