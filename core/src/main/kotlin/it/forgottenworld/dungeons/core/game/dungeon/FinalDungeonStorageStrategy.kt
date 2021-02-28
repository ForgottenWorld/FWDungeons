package it.forgottenworld.dungeons.core.game.dungeon

import it.forgottenworld.dungeons.api.game.dungeon.Dungeon
import it.forgottenworld.dungeons.api.math.Box
import it.forgottenworld.dungeons.api.math.Vector3i
import it.forgottenworld.dungeons.core.config.Storage
import it.forgottenworld.dungeons.core.config.Storage.toConfig
import it.forgottenworld.dungeons.core.game.chest.ChestImpl
import it.forgottenworld.dungeons.core.game.interactiveregion.ActiveAreaImpl
import it.forgottenworld.dungeons.core.game.interactiveregion.TriggerImpl
import org.bukkit.configuration.ConfigurationSection

class FinalDungeonStorageStrategy: Storage.StorageStrategy<FinalDungeon> {

    override fun toConfig(obj: FinalDungeon, config: ConfigurationSection) {

        config.run {
            set("id", obj.id)
            set("name", obj.name)
            set("description", obj.description)
            set("difficulty", obj.difficulty.toString())
            set("points", obj.points)
            set("numberOfPlayers", listOf(
                obj.minPlayers,
                obj.maxPlayers
            ))
            set("width", obj.box.width)
            set("height", obj.box.height)
            set("depth", obj.box.depth)
            set("startingLocation", obj.startingLocation.toVector())
            obj.triggers.values.forEach {
                (it as TriggerImpl).toConfig(createSection("triggers.${it.id}"))
            }
            obj.activeAreas.values.forEach {
                (it as ActiveAreaImpl).toConfig(createSection("activeAreas.${it.id}"))
            }
            obj.chests.values.forEach {
                (it as ChestImpl).toConfig(createSection("chests.${it.id}"))
            }
        }
    }

    override fun fromConfig(config: ConfigurationSection) = config.run {
        val triggers = getConfigurationSection("triggers")
            ?.getKeys(false)
            ?.map { it.toInt() }
            ?.associateWith {
                val sec = getConfigurationSection("triggers.$it")!!
                Storage.load<TriggerImpl>(sec)
            }
            ?: mapOf()

        val activeAreas = getConfigurationSection("activeAreas")
            ?.getKeys(false)
            ?.map { it.toInt() }
            ?.associateWith {
                val sec = getConfigurationSection("activeAreas.$it")!!
                Storage.load<ActiveAreaImpl>(sec)
            }
            ?: mapOf()

        val chests = getConfigurationSection("chests")
            ?.getKeys(false)
            ?.map { it.toInt() }
            ?.associateWith {
                Storage.load<ChestImpl>(getConfigurationSection("chests.$it")!!)
            }
            ?: mapOf()

        val noOfPlayers = getIntegerList("numberOfPlayers")

        val dungeon = FinalDungeon(
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