package it.forgottenworld.dungeons.core.game.dungeon

import com.google.inject.Inject
import com.google.inject.Singleton
import it.forgottenworld.dungeons.api.game.dungeon.Dungeon
import it.forgottenworld.dungeons.api.game.dungeon.EditableDungeon
import it.forgottenworld.dungeons.api.game.dungeon.FinalDungeon
import it.forgottenworld.dungeons.api.game.dungeon.instance.DungeonInstance
import it.forgottenworld.dungeons.api.storage.Storage
import it.forgottenworld.dungeons.api.storage.Storage.Companion.load
import it.forgottenworld.dungeons.api.storage.read
import it.forgottenworld.dungeons.api.storage.yaml
import it.forgottenworld.dungeons.core.config.Strings
import it.forgottenworld.dungeons.core.utils.firstGap
import it.forgottenworld.dungeons.core.utils.launchAsync
import it.forgottenworld.dungeons.core.utils.sendConsoleMessage
import java.util.*

@Singleton
class DungeonManager @Inject constructor(
    private val storage: Storage
) {

    private val finalDungeons = mutableMapOf<Int, FinalDungeon>()
    private val playerEditableDungeons = mutableMapOf<UUID, EditableDungeon>()
    private val dungeonInstances = mutableMapOf<Int, MutableMap<Int, DungeonInstance>>()
    private val playerInstances = mutableMapOf<UUID, DungeonInstance>()

    val finalDungeonCount get() = finalDungeons.size

    fun getFirstAvailableFinalDungeonId() = finalDungeons.keys.firstGap()

    fun clearFinalDungeons() {
        finalDungeons.clear()
    }

    fun enableDungeon(id: Int) {
        finalDungeons[id]?.isActive = true
    }

    fun disableDungeon(id: Int): Boolean {
        val dungeon = finalDungeons[id] ?: return false
        getDungeonInstances(dungeon).values.forEach { it.evacuate() }
        finalDungeons[id]?.isActive = false
        return true
    }

    fun getAllFinalDungeons() = finalDungeons.values

    fun getAllActiveFinalDungeons() = finalDungeons.values.filter { it.isActive }

    fun registerFinalDungeon(dungeon: FinalDungeon) {
        finalDungeons[dungeon.id] = dungeon
    }

    fun getFinalDungeonById(id: Int) = finalDungeons[id]

    fun getAllBusyInstances() = playerInstances.values.distinct()

    fun getDungeonInstances(dungeon: Dungeon): Map<Int, DungeonInstance> {
        dungeonInstances[dungeon.id]?.let { return it }
        return mutableMapOf<Int, DungeonInstance>().also {
            dungeonInstances[dungeon.id] = it
        }
    }

    fun registerDungeonInstance(instance: DungeonInstance) {
        dungeonInstances[instance.dungeon.id]!![instance.id] = instance
    }

    fun clearDungeonInstances(dungeon: Dungeon) {
        dungeonInstances.remove(dungeon.id)
    }

    fun setDungeonInstances(dungeon: Dungeon, instances: Map<Int, DungeonInstance>) {
        dungeonInstances[dungeon.id] = instances.toMutableMap()
    }

    fun getPlayerEditableDungeon(uuid: UUID) = playerEditableDungeons[uuid]

    fun setPlayerEditableDungeon(uuid: UUID, editableDungeon: EditableDungeon?) {
        if (editableDungeon != null) {
            playerEditableDungeons[uuid] = editableDungeon
        } else {
            playerEditableDungeons.remove(uuid)
        }
    }

    fun getPlayerInstance(uuid: UUID) = playerInstances[uuid]

    fun setPlayerInstance(uuid: UUID, dungeonInstance: DungeonInstance?) {
        if (dungeonInstance != null) {
            playerInstances[uuid] = dungeonInstance
        } else {
            playerInstances.remove(uuid)
        }
    }

    fun loadDungeonsFromStorage() {
        for (file in storage.dungeonFiles) {
            try {
                registerFinalDungeon(
                    storage.load<Dungeon>(
                        yaml { load(file) }
                    ) as FinalDungeon
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun loadInstancesFromStorage() {
        yaml {
            load(storage.intancesFile)
            read {
                for (dungeonId in finalDungeons.keys) {
                    var any = false
                    section("$dungeonId") {
                        forEachSection { _, section ->
                            any = true
                            storage.load<DungeonInstance>(section)
                        }
                    }
                    if (any) continue
                    sendConsoleMessage(
                        "${Strings.CONSOLE_PREFIX}Dungeon $dungeonId loaded " +
                            "from config has no instances, create one with " +
                            "/fwde d import $dungeonId"
                    )
                    disableDungeon(dungeonId)
                }
            }
        }
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    fun saveDungeonToStorage(dungeon: FinalDungeon) {
        try {
            yaml {
                storage.save(dungeon, this)
                launchAsync { save(storage.getFileForDungeon(dungeon)) }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}