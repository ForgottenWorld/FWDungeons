package it.forgottenworld.dungeons.core.game.dungeon

import com.google.inject.Inject
import it.forgottenworld.dungeons.api.game.chest.Chest
import it.forgottenworld.dungeons.api.game.dungeon.Dungeon
import it.forgottenworld.dungeons.api.game.interactiveregion.ActiveArea
import it.forgottenworld.dungeons.api.game.interactiveregion.Trigger
import it.forgottenworld.dungeons.api.math.Box
import it.forgottenworld.dungeons.api.math.Vector3i
import it.forgottenworld.dungeons.api.storage.edit
import it.forgottenworld.dungeons.api.storage.read
import it.forgottenworld.dungeons.api.storage.Storage
import it.forgottenworld.dungeons.api.storage.Storage.Companion.load
import org.bukkit.configuration.ConfigurationSection

class FinalDungeonStorageStrategy @Inject constructor(
    private val dungeonFactory: DungeonFactory
): Storage.StorageStrategy<Dungeon> {

    override fun toStorage(obj: Dungeon, config: ConfigurationSection, storage: Storage) {
        config.edit {
            "id" to obj.id
            "name" to obj.name
            "description" to obj.description
            "difficulty" to obj.difficulty.toString()
            "points" to obj.points
            "minPlayers" to obj.minPlayers
            "maxPlayers" to obj.maxPlayers
            "width" to obj.box!!.width
            "height" to obj.box!!.height
            "depth" to obj.box!!.depth
            storage.save(
                obj.startingLocation!!,
                section("startingLocation")
            )
            section("triggers") {
                obj.triggers.values.forEach {
                    storage.save(it, section("${it.id}"))
                }
            }
            section("activeAreas") {
                obj.activeAreas.values.forEach {
                    storage.save(it, section("${it.id}"))
                }
            }
            section("chests") {
                obj.chests.values.forEach {
                    storage.save(it, section("${it.id}"))
                }
            }
        }
    }

    override fun fromStorage(
        config: ConfigurationSection,
        storage: Storage
    ) = config.read {
        val triggers = section("triggers") {
            associateSections { path, tr ->
                path.toInt() to storage.load<Trigger>(tr)
            }
        } ?: mapOf()

        val activeAreas = section("activeAreas") {
            associateSections { path, aa ->
                path.toInt() to storage.load<ActiveArea>(aa)
            }
        } ?: mapOf()

        val chests = section("chests") {
            associateSections { path, c ->
                path.toInt() to storage.load<Chest>(c)
            }
        } ?: mapOf()

        val dungeon = dungeonFactory.createFinal(
            get("id")!!,
            get("name")!!,
            get("description")!!,
            Dungeon.Difficulty.fromString(get("difficulty")!!)!!,
            get("points", 0),
            get("minPlayers")!!,
            get("maxPlayers")!!,
            Box(
                Vector3i.ZERO,
                get("width")!!,
                get("height")!!,
                get("depth")!!
            ),
            storage.load(section("startingLocation")!!),
            triggers,
            activeAreas,
            chests
        )

        dungeon
    }
}