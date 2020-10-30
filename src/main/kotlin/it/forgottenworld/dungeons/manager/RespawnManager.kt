package it.forgottenworld.dungeons.manager

import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.*

object RespawnManager {

    private val playerRespawnLocations = mutableMapOf<UUID, Location>()
    private val playerRespawnGameModes = mutableMapOf<UUID, GameMode>()

    var Player.respawnLocation
        get() = playerRespawnLocations[uniqueId]
        set(value) {
            value?.let { playerRespawnLocations[uniqueId] = it }
                    ?: playerRespawnLocations.remove(uniqueId)
        }

    var UUID.respawnLocation
        get() = playerRespawnLocations[this]
        set(value) {
            value?.let { playerRespawnLocations[this] = it }
                    ?: playerRespawnLocations.remove(this)
        }

    var Player.respawnGameMode
        get() = playerRespawnGameModes[uniqueId]
        set(value) {
            value?.let { playerRespawnGameModes[uniqueId] = it }
                    ?: playerRespawnGameModes.remove(uniqueId)
        }

    var UUID.respawnGameMode
        get() = playerRespawnGameModes[this]
        set(value) {
            value?.let { playerRespawnGameModes[this] = it }
                    ?: playerRespawnGameModes.remove(this)
        }
}