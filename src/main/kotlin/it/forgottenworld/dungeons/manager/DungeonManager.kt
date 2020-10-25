package it.forgottenworld.dungeons.manager

import it.forgottenworld.dungeons.model.Dungeon
import it.forgottenworld.dungeons.model.Party
import it.forgottenworld.dungeons.model.Trigger
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.*

object DungeonManager {

    val dungeons = mutableMapOf<Int, Dungeon>()
    val activeDungeons = mutableMapOf<Int, Boolean>()
    val playerParties = mutableMapOf<UUID, Party>()
    val playersTriggering = mutableMapOf<UUID, Trigger>()
    val playerReturnPositions = mutableMapOf<UUID, Location>()
    val playerReturnGameModes = mutableMapOf<UUID, GameMode>()

    var Player.returnPosition
        get() = playerReturnPositions[uniqueId]
        set(value) {
            value?.let { playerReturnPositions[uniqueId] = it }
                    ?: playerReturnPositions.remove(uniqueId)
        }

    var Player.returnGameMode
        get() = playerReturnGameModes[uniqueId]
        set(value) {
            value?.let { playerReturnGameModes[uniqueId] = it }
                    ?: playerReturnGameModes.remove(uniqueId)
        }

    var Player.party
        get() = playerParties[uniqueId]
        set(value) {
            value?.let { playerParties[uniqueId] = it }
                    ?: playerParties.remove(uniqueId)
        }

    var Player.collidingTrigger
        get() = playersTriggering[uniqueId]
        set(value) {
            value?.let { playersTriggering[uniqueId] = it }
                    ?: playersTriggering.remove(uniqueId)
        }

    val Player.dungeonInstance
        get() = playerParties[uniqueId]?.instance

    val maxDungeonId
        get() = dungeons.keys.maxOrNull() ?: -1

    fun getDungeonById(id: Int) = dungeons[id]

    fun evacuateDungeon(dungeonId: Int, instanceId: Int): Boolean {
        dungeons[dungeonId]
                ?.instances
                ?.find { it.id == instanceId }
                ?.onInstanceFinish(false)
                ?: return false
        return true
    }
}


