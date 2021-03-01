package it.forgottenworld.dungeons.core.game

import it.forgottenworld.dungeons.api.game.dungeon.Dungeon
import it.forgottenworld.dungeons.api.game.instance.DungeonInstance
import it.forgottenworld.dungeons.core.game.dungeon.EditableDungeon
import it.forgottenworld.dungeons.core.game.dungeon.FinalDungeon
import it.forgottenworld.dungeons.core.game.instance.DungeonInstanceImpl
import java.util.*

object DungeonManager {

    val finalDungeons = mutableMapOf<Int, FinalDungeon>()

    private val playerEditableDungeons = mutableMapOf<UUID, EditableDungeon>()
    private val dungeonInstances = mutableMapOf<Int, Map<Int, DungeonInstance>>()
    val playerFinalInstances = mutableMapOf<UUID, DungeonInstance>()

    fun getDungeonInstances(dungeon: Dungeon): Map<Int, DungeonInstance> {
        dungeonInstances[dungeon.id]?.let { return it }
        val newMap = mapOf<Int, DungeonInstanceImpl>()
        dungeonInstances[dungeon.id] = newMap
        return newMap
    }

    fun setDungeonInstances(dungeon: Dungeon, instances: Map<Int, DungeonInstance>) {
        dungeonInstances[dungeon.id] = instances
    }

    var UUID.editableDungeon: EditableDungeon?
        get() = playerEditableDungeons[this]
        set(value) {
            if (value != null) {
                playerEditableDungeons[this] = value
            } else {
                playerEditableDungeons.remove(this)
            }
        }

    var UUID.finalInstance
        get() = playerFinalInstances[this]
        set(value) {
            if (value != null) {
                playerFinalInstances[this] = value
            } else {
                playerFinalInstances.remove(this)
            }
        }
}