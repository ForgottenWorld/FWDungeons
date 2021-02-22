package it.forgottenworld.dungeons.core.game.dungeon

import it.forgottenworld.dungeons.api.game.dungeon.Dungeon
import it.forgottenworld.dungeons.core.game.instance.DungeonInstanceImpl
import org.bukkit.entity.Player
import java.util.*

object DungeonManager {

    val finalDungeons = mutableMapOf<Int, FinalDungeon>()

    private val playerEditableDungeons = mutableMapOf<UUID, EditableDungeon>()
    private val dungeonInstances = mutableMapOf<Int, Map<Int, DungeonInstanceImpl>>()
    val playerFinalInstances = mutableMapOf<UUID, DungeonInstanceImpl>()

    var Dungeon.instances: Map<Int, DungeonInstanceImpl>
        get() = dungeonInstances[id]
            ?: mutableMapOf<Int, DungeonInstanceImpl>()
                .also { dungeonInstances[id] = it }
        set(value) {
            dungeonInstances[id] = value
        }

    var Player.editableDungeon: EditableDungeon?
        get() = playerEditableDungeons[uniqueId]
        set(value) {
            value?.let {
                playerEditableDungeons[uniqueId] = it
            } ?: playerEditableDungeons.remove(uniqueId)
        }

    var Player.finalInstance
        get() = playerFinalInstances[uniqueId]
        set(value) {
            value?.let {
                playerFinalInstances[uniqueId] = value
            } ?: playerFinalInstances.remove(uniqueId)
        }

    var UUID.finalInstance
        get() = playerFinalInstances[this]
        set(value) {
            value?.let {
                playerFinalInstances[this] = value
            } ?: playerFinalInstances.remove(this)
        }
}