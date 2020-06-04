package it.forgottenworld.dungeons.model.dungeon

import it.forgottenworld.dungeons.model.activearea.ActiveArea
import it.forgottenworld.dungeons.model.box.Box
import it.forgottenworld.dungeons.model.trigger.Trigger
import org.bukkit.util.BlockVector

class Dungeon(val id: Int) {
    var name = ""
    var description = ""
    lateinit var difficulty: Difficulty
    lateinit var numberOfPlayers: IntRange
    lateinit var box: Box
    lateinit var startingLocation: BlockVector
    lateinit var startingEffect: () -> Unit
    var triggers = mutableListOf<Trigger>()
    var activeAreas = mutableListOf<ActiveArea>()
    var instances = mutableListOf<DungeonInstance>()

    enum class Difficulty {
        EASY, MEDIUM, HARD;

        companion object {
            fun fromString(value: String) =
                 when (value) {
                     "easy" -> EASY
                     "medium" -> MEDIUM
                     "hard" -> HARD
                     else -> null
                 }
        }

        override fun toString() =
            when (this) {
                EASY -> "easy"
                MEDIUM -> "medium"
                HARD -> "hard"
            }
    }

    fun hasBox() = ::box.isInitialized

    constructor(id: Int,
                name: String,
                description: String,
                difficulty: Difficulty,
                numberOfPlayers: IntRange,
                box: Box,
                startingLocation: BlockVector,
                startingEffect: () -> Unit,
                triggers: MutableList<Trigger>,
                activeAreas: MutableList<ActiveArea>,
                instances: MutableList<DungeonInstance>) : this(id) {
        this.name = name
        this.description = description
        this.difficulty = difficulty
        this.numberOfPlayers = numberOfPlayers
        this.box = box
        this.startingLocation = startingLocation
        this.triggers = triggers
        this.activeAreas = activeAreas
        this.instances = instances
    }

    fun whatIsMissingForWriteout(): String {
        var res = ""
        if (name == "") res += "name, "
        if (!::difficulty.isInitialized) res += "difficulty, "
        if (!::numberOfPlayers.isInitialized) res += "number of players, "
        if (!hasBox()) res += "box, "
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