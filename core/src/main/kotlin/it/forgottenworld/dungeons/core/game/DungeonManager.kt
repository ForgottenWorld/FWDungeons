package it.forgottenworld.dungeons.core.game

import com.google.inject.Singleton
import it.forgottenworld.dungeons.api.game.dungeon.Dungeon
import it.forgottenworld.dungeons.api.game.dungeon.EditableDungeon
import it.forgottenworld.dungeons.api.game.dungeon.FinalDungeon
import it.forgottenworld.dungeons.api.game.instance.DungeonInstance
import java.util.*

@Singleton
class DungeonManager {

    val finalDungeons = mutableMapOf<Int, FinalDungeon>()

    private val playerEditableDungeons = mutableMapOf<UUID, EditableDungeon>()
    private val dungeonInstances = mutableMapOf<Int, Map<Int, DungeonInstance>>()
    val playerInstances = mutableMapOf<UUID, DungeonInstance>()

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