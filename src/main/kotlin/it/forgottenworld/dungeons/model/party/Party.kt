package it.forgottenworld.dungeons.model.party

import it.forgottenworld.dungeons.model.dungeon.DungeonInstance
import it.forgottenworld.dungeons.state.DungeonState.collidingTrigger
import it.forgottenworld.dungeons.state.DungeonState.party
import it.forgottenworld.dungeons.state.DungeonState.returnGameMode
import it.forgottenworld.dungeons.state.DungeonState.returnPosition
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
        get() = players.count()

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

    fun playerJoin(player: Player): Boolean {
        if (isFull || players.contains(player)) return false
        players.forEach { it.sendFWDMessage("${player.name} joined the dungeon party") }
        player.party = this
        return players.add(player)
    }

    fun playerLeave(player: Player) {
        players.remove(player.apply {
            returnPosition = null
            returnGameMode = null
            returnGameMode = null
            collidingTrigger?.onPlayerExit(player)
            collidingTrigger = null
        })
        players.forEach { it.sendFWDMessage("${player.name} left the dungeon party") }
        if (leader != player) return
        if (players.isEmpty()) instance.resetInstance()
        else leader = players.first().apply { sendFWDMessage("You're now the party leader") }
    }

    fun playerDied(player: Player) {
        players.remove(player.apply {
            returnPosition = null
            returnGameMode = null
            returnGameMode = null
            collidingTrigger?.onPlayerExit(player)
            collidingTrigger = null
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
        } }
        players.clear()
    }
}