package it.forgottenworld.dungeons.model

import it.forgottenworld.dungeons.manager.DungeonManager.collidingTrigger
import it.forgottenworld.dungeons.manager.DungeonManager.party
import it.forgottenworld.dungeons.manager.DungeonManager.returnGameMode
import it.forgottenworld.dungeons.manager.DungeonManager.returnPosition
import it.forgottenworld.dungeons.manager.RespawnManager.respawnGameMode
import it.forgottenworld.dungeons.manager.RespawnManager.respawnLocation
import it.forgottenworld.dungeons.utils.getRandomString
import it.forgottenworld.dungeons.utils.sendFWDMessage
import org.bukkit.entity.Player

class Party(
        val players: MutableList<Player> = mutableListOf(),
        var leader: Player,
        val maxPlayers: Int,
        var isLocked: Boolean,
        val instance: DungeonInstance) {

    var inGame = false
    var partyKey = ""

    val playerCount: Int
        get() = players.size

    val isFull: Boolean
        get() = playerCount == maxPlayers

    fun lock() {
        isLocked = true
        partyKey = getRandomString(10)
    }

    fun unlock() {
        isLocked = false
        partyKey = ""
    }

    fun onPlayerJoin(player: Player): Boolean {
        if (isFull || players.contains(player)) return false
        players.forEach { it.sendFWDMessage("${player.name} joined the dungeon party") }
        player.party = this
        return players.add(player)
    }

    fun onPlayerLeave(player: Player) {
        players.remove(player.apply {
            returnPosition = null
            returnGameMode = null
            returnGameMode = null
            collidingTrigger?.onPlayerExit(player)
            collidingTrigger = null
            party = null
        })
        players.forEach { it.sendFWDMessage("${player.name} left the dungeon party") }
        if (leader != player) return
        if (players.isEmpty()) instance.resetInstance()
        else leader = players.first().apply { sendFWDMessage("You're now the party leader") }
    }

    fun playerDied(player: Player) {
        players.remove(player.apply {
            returnPosition?.let { respawnLocation = it }
            returnGameMode?.let { respawnGameMode = it }
            returnPosition = null
            returnGameMode = null
            collidingTrigger?.onPlayerExit(player)
            collidingTrigger = null
            party = null
        })
        players.forEach { it.sendFWDMessage("${player.name} died in the dungeon") }
        if (leader != player) return
        if (players.isEmpty()) instance.resetInstance()
        else leader = players.first().apply { sendFWDMessage("You're now the party leader") }
    }

    fun disband() {
        players.forEach { it.run {
            returnPosition = null
            returnGameMode = null
            returnGameMode = null
            collidingTrigger = null
            party = null
        } }
        players.clear()
    }
}