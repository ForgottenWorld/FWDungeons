package it.forgottenworld.dungeons.core.game.dungeon.subelement.interactiveregion.spawnarea

import com.google.inject.Inject
import it.forgottenworld.dungeons.api.game.dungeon.subelement.interactiveregion.SpawnArea
import it.forgottenworld.dungeons.api.storage.Storage
import it.forgottenworld.dungeons.api.storage.Storage.Companion.load
import it.forgottenworld.dungeons.api.storage.Storage.Companion.save
import it.forgottenworld.dungeons.api.storage.edit
import it.forgottenworld.dungeons.api.storage.read
import org.bukkit.configuration.ConfigurationSection

class SpawnAreaStorageStrategy @Inject constructor(
    private val spawnAreaFactory: SpawnAreaFactory
) : Storage.StorageStrategy<SpawnArea> {

    override fun toStorage(
        obj: SpawnArea,
        config: ConfigurationSection,
        storage: Storage
    ) {
        config.edit { 
            "id" to obj.id
            obj.label?.let { l -> "label" to l }
            storage.save(obj.box, section("box"))
            section("heightMap") {
                for ((x,z) in obj.heightMap.withIndex()) {
                    "$x" to z.joinToString(",")
                }
            }
        }
    }

    override fun fromStorage(config: ConfigurationSection, storage: Storage) = config.read {
        val heightMap = section("heightMap") {
            mapKeys { k ->
                get<String>(k)!!
                    .split(",")
                    .map { it.toInt() }
                    .toIntArray()
            }.toTypedArray()
        } ?: arrayOf()
        spawnAreaFactory.create(
            get("id")!!,
            storage.load(section("box")!!),
            heightMap,
            get("label")
        )
    }
}