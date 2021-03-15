package it.forgottenworld.dungeons.core.game.dungeon

import com.google.inject.Inject
import it.forgottenworld.dungeons.api.game.chest.Chest
import it.forgottenworld.dungeons.api.game.dungeon.Dungeon
import it.forgottenworld.dungeons.api.game.dungeon.FinalDungeon
import it.forgottenworld.dungeons.api.game.interactiveregion.ActiveArea
import it.forgottenworld.dungeons.api.game.interactiveregion.Trigger
import it.forgottenworld.dungeons.api.math.Box
import it.forgottenworld.dungeons.api.math.Vector3i
import it.forgottenworld.dungeons.api.storage.Storage
import it.forgottenworld.dungeons.api.storage.Storage.Companion.load
import it.forgottenworld.dungeons.api.storage.Storage.Companion.save
import it.forgottenworld.dungeons.api.storage.edit
import it.forgottenworld.dungeons.api.storage.read
import org.bukkit.configuration.ConfigurationSection

class FinalDungeonStorageStrategy @Inject constructor(
    private val dungeonFactory: DungeonFactory
): Storage.StorageStrategy<FinalDungeon> {

    override fun toStorage(obj: FinalDungeon, config: ConfigurationSection, storage: Storage) {
        config.edit {
            "id" to obj.id
            "name" to obj.name
            "description" to obj.description
            "difficulty" to obj.difficulty.toString()
            "points" to obj.points
            "minPlayers" to obj.minPlayers
            "maxPlayers" to obj.maxPlayers
            "width" to obj.box.width
            "unlockableSeriesId" to obj.unlockableSeriesId
            "unlockableId" to obj.unlockableId
            "height" to obj.box.height
            "depth" to obj.box.depth
            storage.save(
                obj.startingLocation,
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
            id = get("id")!!,
            name = get("name")!!,
            description = get("description")!!,
            difficulty = Dungeon.Difficulty.fromString(get("difficulty")!!)!!,
            points = get("points", 0),
            minPlayers = get("minPlayers")!!,
            maxPlayers = get("maxPlayers")!!,
            box = Box(
                Vector3i.ZERO,
                get("width")!!,
                get("height")!!,
                get("depth")!!
            ),
            startingLocation = storage.load(section("startingLocation")!!),
            triggers = triggers,
            activeAreas = activeAreas,
            chests = chests,
            unlockableSeriesId = get("unlockableSeriesId"),
            unlockableId = get("unlockableId")
        )

        dungeon
    }
}