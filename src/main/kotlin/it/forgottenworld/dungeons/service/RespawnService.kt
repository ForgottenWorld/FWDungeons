package it.forgottenworld.dungeons.service

import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.*

object RespawnService {

    private val playerRespawnLocations = mutableMapOf<UUID, Location>()
    private val playerRespawnGameModes = mutableMapOf<UUID, GameMode>()

    var Player.respawnLocation
        get() = playerRespawnLocations[uniqueId]
        set(value) {
            value?.let { playerRespawnLocations[uniqueId] = it }
                    ?: playerRespawnLocations.remove(uniqueId)
        }

    var Player.respawnGameMode
        get() = playerRespawnGameModes[uniqueId]
        set(value) {
            value?.let { playerRespawnGameModes[uniqueId] = it }
                    ?: playerRespawnGameModes.remove(uniqueId)
        }
}