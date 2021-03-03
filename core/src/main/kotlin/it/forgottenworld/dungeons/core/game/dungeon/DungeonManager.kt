package it.forgottenworld.dungeons.core.game.dungeon

import com.google.inject.Singleton
import it.forgottenworld.dungeons.api.game.dungeon.Dungeon
import it.forgottenworld.dungeons.api.game.dungeon.EditableDungeon
import it.forgottenworld.dungeons.api.game.dungeon.FinalDungeon
import it.forgottenworld.dungeons.api.game.dungeon.instance.DungeonInstance
import it.forgottenworld.dungeons.core.utils.firstGap
import java.util.*

@Singleton
class DungeonManager {

    private val finalDungeons = mutableMapOf<Int, FinalDungeon>()
    private val playerEditableDungeons = mutableMapOf<UUID, EditableDungeon>()
    private val dungeonInstances = mutableMapOf<Int, Map<Int, DungeonInstance>>()
    private val playerInstances = mutableMapOf<UUID, DungeonInstance>()

    val finalDungeonIds get() = finalDungeons.keys

    val finalDungeonCount get() = finalDungeons.size

    fun getFirstAvailableFinalDungeonId() = finalDungeons.keys.firstGap()

    fun clearFinalDungeons() {
        finalDungeons.clear()
    }

    fun enableDungeon(id: Int) {
        finalDungeons[id]?.isActive = true
    }

    fun disableDungeon(id: Int) {
        finalDungeons[id]?.isActive = false
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
        val newMap = mapOf<Int, DungeonInstance>()
        dungeonInstances[dungeon.id] = newMap
        return newMap
    }

    fun setDungeonInstances(dungeon: Dungeon, instances: Map<Int, DungeonInstance>) {
        dungeonInstances[dungeon.id] = instances
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

}