package it.forgottenworld.dungeons.core.game.dungeon

import com.google.inject.Inject
import it.forgottenworld.dungeons.api.game.dungeon.Dungeon
import it.forgottenworld.dungeons.api.game.interactiveregion.ActiveArea
import it.forgottenworld.dungeons.api.math.Box
import it.forgottenworld.dungeons.api.math.Vector3i
import it.forgottenworld.dungeons.api.storage.Storage
import it.forgottenworld.dungeons.api.storage.Storage.Companion.load
import it.forgottenworld.dungeons.core.game.chest.ChestImpl
import it.forgottenworld.dungeons.core.game.interactiveregion.TriggerImpl
import org.bukkit.configuration.ConfigurationSection

class FinalDungeonStorageStrategy @Inject constructor(
    private val dungeonFactory: DungeonFactory
): Storage.StorageStrategy<Dungeon> {

    override fun toConfig(obj: Dungeon, config: ConfigurationSection, storage: Storage) {
        config.set("id", obj.id)
        config.set("name", obj.name)
        config.set("description", obj.description)
        config.set("difficulty", obj.difficulty.toString())
        config.set("points", obj.points)
        config.set("numberOfPlayers", listOf(
            obj.minPlayers,
            obj.maxPlayers
        ))
        config.set("width", obj.box!!.width)
        config.set("height", obj.box!!.height)
        config.set("depth", obj.box!!.depth)
        config.set("startingLocation", obj.startingLocation!!.toVector())
        obj.triggers.values.forEach {
            storage.save(it, config.createSection("triggers.${it.id}"))
        }
        obj.activeAreas.values.forEach {
            storage.save(it, config.createSection("activeAreas.${it.id}"))
        }
        obj.chests.values.forEach {
            storage.save(it, config.createSection("chests.${it.id}"))
        }
    }

    override fun fromConfig(config: ConfigurationSection, storage: Storage) = config.run {
        val triggers = getConfigurationSection("triggers")
            ?.getKeys(false)
            ?.map { it.toInt() }
            ?.associateWith {
                storage.load<TriggerImpl>(getConfigurationSection("triggers.$it")!!)
            }
            ?: mapOf()

        val activeAreas = getConfigurationSection("activeAreas")
            ?.getKeys(false)
            ?.map { it.toInt() }
            ?.associateWith {
                storage.load<ActiveArea>(getConfigurationSection("activeAreas.$it")!!)
            }
            ?: mapOf()

        val chests = getConfigurationSection("chests")
            ?.getKeys(false)
            ?.map { it.toInt() }
            ?.associateWith {
                storage.load<ChestImpl>(getConfigurationSection("chests.$it")!!)
            }
            ?: mapOf()

        val noOfPlayers = getIntegerList("numberOfPlayers")

        val dungeon = dungeonFactory.createFinal(
            getInt("id"),
            getString("name")!!,
            getString("description")!!,
            Dungeon.Difficulty.fromString(getString("difficulty")!!)!!,
            getInt("points", 0),
            noOfPlayers[0],
            noOfPlayers[1],
            Box(
                Vector3i.ZERO,
                getInt("width"),
                getInt("height"),
                getInt("depth")
            ),
            Vector3i.ofBukkitVector(getVector("startingLocation")!!),
            triggers,
            activeAreas,
            chests
        )

        dungeon
    }
}