package it.forgottenworld.dungeons.model

import it.forgottenworld.dungeons.manager.DungeonManager
import it.forgottenworld.dungeons.scripting.parseCode
import it.forgottenworld.dungeons.utils.toVector
import org.bukkit.Material
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.util.BlockVector

class Dungeon(val id: Int) {
    var name = ""
    var description = ""
    lateinit var difficulty: Difficulty
    lateinit var numberOfPlayers: IntRange
    lateinit var box: Box
    var startingLocation: BlockVector? = null
    var triggers = mutableListOf<Trigger>()
    var activeAreas = mutableListOf<ActiveArea>()
    var instances = mutableListOf<DungeonInstance>()
    var points = 0

    enum class Difficulty {
        EASY, MEDIUM, HARD;

        companion object {
            fun fromString(value: String) = when (value) {
                "easy" -> EASY
                "medium" -> MEDIUM
                "hard" -> HARD
                else -> null
            }
        }

        override fun toString() = when (this) {
            EASY -> "easy"
            MEDIUM -> "medium"
            HARD -> "hard"
        }
    }

    companion object {

        fun fromConfig(conf: YamlConfiguration) =
            Dungeon(
            conf.getInt("id"),
            conf.getString("name")!!,
            conf.getString("description")!!,
            Difficulty.fromString(conf.getString("difficulty")!!)!!,
            conf.getInt("points", 0),
            conf.getIntegerList("numberOfPlayers").let{ IntRange(it.first(), it.last()) },
            Box(
                BlockVector(0,0,0),
                conf.getInt("width"),
                conf.getInt("height"),
                conf.getInt("depth")
            ),
            conf.getVector("startingLocation")!!.toBlockVector(),
            mutableListOf(),
            mutableListOf(),
            mutableListOf()
            ).apply {
                triggers.addAll(
                        conf.getConfigurationSection("triggers")
                        !!.getKeys(false).map { k ->
                            Trigger(
                                    k.toInt(),
                                    this,
                                    Box(
                                            conf.getVector("triggers.$k.origin")!!.toBlockVector(),
                                            conf.getInt("triggers.$k.width"),
                                            conf.getInt("triggers.$k.height"),
                                            conf.getInt("triggers.$k.depth")
                                    ),
                                    parseCode(conf.getStringList("triggers.$k.effect")),
                                    conf.getBoolean("triggers.$k.requiresWholeParty")
                            ).apply {
                                conf.getString("triggers.$k.label")?.let {
                                    label = it
                                }
                            }
                        }
                )
                activeAreas.addAll(
                        conf.getConfigurationSection("activeAreas")
                        !!.getKeys(false)
                                .map { k ->
                                    ActiveArea(
                                            k.toInt(),
                                            Box(
                                                    conf.getVector("activeAreas.$k.origin")!!.toBlockVector(),
                                                    conf.getInt("activeAreas.$k.width"),
                                                    conf.getInt("activeAreas.$k.height"),
                                                    conf.getInt("activeAreas.$k.depth")
                                            ),
                                            Material.getMaterial(conf.getString("activeAreas.$k.startingMaterial")!!)!!
                                    ).apply {
                                        conf.getString("activeAreas.$k.label")?.let {
                                            label = it
                                        }
                                    }
                                }
                )
                DungeonManager.activeDungeons[id] = true
            }
    }

    val hasBox get() = ::box.isInitialized

    constructor(id: Int,
                name: String,
                description: String,
                difficulty: Difficulty,
                points: Int,
                numberOfPlayers: IntRange,
                box: Box,
                startingLocation: BlockVector,
                triggers: MutableList<Trigger>,
                activeAreas: MutableList<ActiveArea>,
                instances: MutableList<DungeonInstance>) : this(id) {
        this.name = name
        this.description = description
        this.difficulty = difficulty
        this.points = points
        this.numberOfPlayers = numberOfPlayers
        this.box = box
        this.startingLocation = startingLocation
        this.triggers = triggers
        this.activeAreas = activeAreas
        this.instances = instances
    }

    fun toConfig(conf: YamlConfiguration, eraseEffects: Boolean) {
        val dungeon = this
        conf.run {
            set("id", dungeon.id)
            set("name", dungeon.name)
            set("description", dungeon.description)
            set("difficulty", dungeon.difficulty.toString())
            set("points", dungeon.points)
            set("numberOfPlayers", listOf(dungeon.numberOfPlayers.first, dungeon.numberOfPlayers.last))
            set("width", dungeon.box.width)
            set("height", dungeon.box.height)
            set("depth", dungeon.box.depth)
            set("startingLocation", dungeon.startingLocation?.toVector())
            dungeon.triggers.forEach {
                set("triggers.${it.id}.id", it.id)
                it.label?.let { l -> set("triggers.${it.id}.label", l) }
                set("triggers.${it.id}.origin", it.origin.toVector())
                set("triggers.${it.id}.width", it.box.width)
                set("triggers.${it.id}.height", it.box.height)
                set("triggers.${it.id}.depth", it.box.depth)
                if (eraseEffects) set("triggers.${it.id}.effect", "")
                set("triggers.${it.id}.requiresWholeParty", it.requiresWholeParty)
            }
            dungeon.activeAreas.forEach {
                set("activeAreas.${it.id}.id", it.id)
                it.label?.let { l -> set("activeAreas.${it.id}.label", l) }
                set("activeAreas.${it.id}.origin", it.box.origin.toVector())
                set("activeAreas.${it.id}.width", it.box.width)
                set("activeAreas.${it.id}.height", it.box.height)
                set("activeAreas.${it.id}.depth", it.box.depth)
                set("activeAreas.${it.id}.startingMaterial", it.startingMaterial.name)
            }
        }
    }

    fun whatIsMissingForWriteout() = StringBuilder().apply {
        if (name.isEmpty()) append("name, ")
        if (!::difficulty.isInitialized) append("difficulty, ")
        if (!::numberOfPlayers.isInitialized) append("number of players, ")
        if (!hasBox) append("box, ")
        if (startingLocation == null) append("starting location, ")
        if (triggers.isEmpty()) append("at least one trigger, ")
        if (activeAreas.isEmpty()) append("at least one active area, ")
    }.toString().dropLast(2)
}