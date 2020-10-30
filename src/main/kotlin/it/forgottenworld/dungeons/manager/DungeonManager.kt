package it.forgottenworld.dungeons.manager

import it.forgottenworld.dungeons.model.dungeon.FinalDungeon
import it.forgottenworld.dungeons.model.instance.DungeonFinalInstance
import it.forgottenworld.dungeons.model.interactiveelement.Trigger
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.*

object DungeonManager {

    val dungeons = mutableMapOf<Int, FinalDungeon>()

    val playerInstances = mutableMapOf<UUID, DungeonFinalInstance>()
    val playersTriggering = mutableMapOf<UUID, Trigger>()
    val playerReturnPositions = mutableMapOf<UUID, Location>()
    val playerReturnGameModes = mutableMapOf<UUID, GameMode>()

    var Player.returnPosition
        get() = playerReturnPositions[uniqueId]
        set(value) {
            value?.let { playerReturnPositions[uniqueId] = it }
                    ?: playerReturnPositions.remove(uniqueId)
        }

    var UUID.returnPosition
        get() = playerReturnPositions[this]
        set(value) {
            value?.let { playerReturnPositions[this] = it }
                    ?: playerReturnPositions.remove(this)
        }

    var Player.returnGameMode
        get() = playerReturnGameModes[uniqueId]
        set(value) {
            value?.let { playerReturnGameModes[uniqueId] = it }
                    ?: playerReturnGameModes.remove(uniqueId)
        }

    var UUID.returnGameMode
        get() = playerReturnGameModes[this]
        set(value) {
            value?.let { playerReturnGameModes[this] = it }
                    ?: playerReturnGameModes.remove(this)
        }

    var Player.collidingTrigger
        get() = playersTriggering[uniqueId]
        set(value) {
            value?.let { playersTriggering[uniqueId] = it }
                    ?: playersTriggering.remove(uniqueId)
        }

    var UUID.collidingTrigger
        get() = playersTriggering[this]
        set(value) {
            value?.let { playersTriggering[this] = it }
                    ?: playersTriggering.remove(this)
        }

    var Player.dungeonInstance
        get() = playerInstances[uniqueId]
        set(value) {
            value?.let {
                playerInstances[uniqueId] = value
            } ?: playerInstances.remove(uniqueId)
        }

    var UUID.dungeonInstance
        get() = playerInstances[this]
        set(value) {
            value?.let {
                playerInstances[this] = value
            } ?: playerInstances.remove(this)
        }
}


