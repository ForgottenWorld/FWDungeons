package it.forgottenworld.dungeons.model.dungeon

import it.forgottenworld.dungeons.FWDungeonsPlugin
import it.forgottenworld.dungeons.model.box.Box
import it.forgottenworld.dungeons.model.instance.DungeonFinalInstance
import it.forgottenworld.dungeons.model.interactiveelement.ActiveArea
import it.forgottenworld.dungeons.model.interactiveelement.Trigger
import it.forgottenworld.dungeons.service.DungeonService
import it.forgottenworld.dungeons.utils.blockVector
import it.forgottenworld.dungeons.utils.bukkitThreadAsync
import it.forgottenworld.dungeons.utils.toVector
import org.bukkit.Bukkit
import org.bukkit.block.Block
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.util.BlockVector
import java.io.File

class FinalDungeon(val id: Int,
                   val name: String,
                   val description: String,
                   val difficulty: Difficulty,
                   val points: Int,
                   val numberOfPlayers: IntRange,
                   val box: Box,
                   val startingLocation: BlockVector,
                   private val triggers: Map<Int, Trigger>,
                   private val activeAreas: Map<Int, ActiveArea>,
                   var instances: Map<Int,DungeonFinalInstance>) : Dungeon {

    var active = true

    companion object {

        fun fromConfig(conf: YamlConfiguration): FinalDungeon = conf.run {
            val triggers = getConfigurationSection("triggers")!!
                    .getKeys(false)
                    .map { it.toInt() to Trigger.fromConfig(it.toInt(), getConfigurationSection("triggers.$it")!!) }
                    .toMap()

            val activeAreas = getConfigurationSection("activeAreas")!!
                    .getKeys(false)
                    .map { it.toInt() to ActiveArea.fromConfig(it.toInt(), getConfigurationSection("activeAreas.$it")!!) }
                    .toMap()

            val dungeon = FinalDungeon(
                    getInt("id"),
                    getString("name")!!,
                    getString("description")!!,
                    Difficulty.fromString(getString("difficulty")!!)!!,
                    getInt("points", 0),
                    getIntegerList("numberOfPlayers").let { IntRange(it.first(), it.last()) },
                    Box(BlockVector(0, 0, 0), getInt("width"), getInt("height"), getInt("depth")),
                    getVector("startingLocation")!!.toBlockVector(),
                    triggers,
                    activeAreas,
                    mapOf()
            )

            dungeon
        }
    }

    fun makeEditable(): EditableDungeon? {
        if (active) return null
        DungeonService.dungeons.remove(id)

        return EditableDungeon(-1001).also {
            it.name = name
            it.description = description
            it.difficulty = difficulty
            it.points = points
            it.numberOfPlayers = numberOfPlayers
            it.box = box
            it.startingLocation = startingLocation
            it.triggers = triggers.toMutableMap()
            it.activeAreas = activeAreas.toMutableMap()
            it.finalInstanceLocations = instances.map { (k,v) -> k to v.origin }.toMap().toMutableMap()
        }
    }

    fun createInstance(target: Block, predefinedId: Int? = null): DungeonFinalInstance {
        val id = predefinedId ?: instances.keys.lastOrNull()?.plus(1) ?: 0
        val newOrigin = target.blockVector

        val newTriggers = triggers
                .map { (k,v) -> k to v.withContainerOrigin(BlockVector(0,0,0), newOrigin) }
                .toMap()

        val newActiveAreas = activeAreas
                .map { (k,v) -> k to v.withContainerOrigin(BlockVector(0,0,0), newOrigin) }
                .toMap()

        val newInstance = DungeonFinalInstance(
                id,
                this,
                newOrigin,
                newTriggers,
                newActiveAreas
        )

        if (predefinedId == null) {
            try {
                val config = YamlConfiguration()
                val file = File(FWDungeonsPlugin.pluginDataFolder, "instances.yml")
                if (file.exists()) config.load(file)
                newInstance.toConfig(config.createSection("${id}-${newInstance.id}"))
                bukkitThreadAsync { config.save(file) }
            } catch (e: Exception) {
                Bukkit.getLogger().warning(e.message)
            }
        }

        newInstance.resetInstance()
        val newInstances = instances + (id to newInstance)
        instances = newInstances
        return newInstance
    }

    fun toConfig(conf: YamlConfiguration, eraseEffects: Boolean) {
        val dungeon = this
        conf.run {
            set("id", dungeon.id)
            set("name", dungeon.name)
            set("description", dungeon.description)
            set("difficulty", dungeon.difficulty.toString())
            set("points", dungeon.points)
            set("numberOfPlayers", dungeon.numberOfPlayers.toList())
            set("width", dungeon.box.width)
            set("height", dungeon.box.height)
            set("depth", dungeon.box.depth)
            set("startingLocation", dungeon.startingLocation.toVector())
            dungeon.triggers.values.forEach {
                if (contains("triggers.${it.id}")) createSection("triggers.${it.id}")
                it.toConfig(getConfigurationSection("triggers.${it.id}")!!, eraseEffects)
            }
            dungeon.activeAreas.values.forEach {
                if (contains("activeAreas.${it.id}")) createSection("activeAreas.${it.id}")
                it.toConfig(getConfigurationSection("activeAreas.${it.id}")!!)
            }
        }
    }
}