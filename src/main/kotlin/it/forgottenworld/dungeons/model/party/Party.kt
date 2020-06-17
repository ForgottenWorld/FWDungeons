package it.forgottenworld.dungeons.model.party

import it.forgottenworld.dungeons.state.DungeonState
import it.forgottenworld.dungeons.cui.StringConst
import it.forgottenworld.dungeons.cui.getString
import it.forgottenworld.dungeons.model.dungeon.DungeonInstance
import it.forgottenworld.dungeons.utils.getRandomString
import org.bukkit.entity.Player

class Party(
        val players: MutableList<Player> = mutableListOf(),
        var leader: Player,
        val maxPlayers: Int,
        var isLocked: Boolean,
        val instance: DungeonInstance
) {
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

    fun playerJoin(player: Player) : Boolean {
        if (players.count() == maxPlayers) return false
        return if (players.contains(player)) false
        else {
            players.forEach { it.sendMessage("${getString(StringConst.CHAT_PREFIX)}${player.name} joined the dungeon party") }
            DungeonState.playerParties[player.uniqueId] = this
            players.add(player)
        }
    }

    fun playerLeave(player: Player) {
        players.remove(player)
        DungeonState.playerReturnPositions.remove(player.uniqueId)
        DungeonState.playerReturnGameModes.remove(player.uniqueId)
        DungeonState.playerParties.remove(player.uniqueId)
        DungeonState.playersTriggering[player.uniqueId]?.onPlayerExit(player)
        DungeonState.playersTriggering.remove(player.uniqueId)
        players.forEach { it.sendMessage("${getString(StringConst.CHAT_PREFIX)}${player.name} left the dungeon party") }
        if (leader == player) {
            if (players.isEmpty()) {
                instance.resetInstance()
            } else {
                leader = players.first().apply { sendMessage("${getString(StringConst.CHAT_PREFIX)}You're now the party leader") }
            }
        }
    }

    fun playerDied(player: Player) {
        players.remove(player)
        DungeonState.playerReturnPositions.remove(player.uniqueId)
        DungeonState.playerReturnGameModes.remove(player.uniqueId)?.let { player.gameMode = it }
        DungeonState.playerParties.remove(player.uniqueId)
        DungeonState.playersTriggering[player.uniqueId]?.onPlayerExit(player)
        DungeonState.playersTriggering.remove(player.uniqueId)
        players.forEach { it.sendMessage("${getString(StringConst.CHAT_PREFIX)}${player.name} died in the dungeon") }
        if (leader == player) {
            if (players.isEmpty()) {
                instance.resetInstance()
            } else {
                leader = players.first().apply { sendMessage("${getString(StringConst.CHAT_PREFIX)}You're now the party leader") }
            }
        }
    }

    fun disband() {
        players.forEach {
            DungeonState.playerReturnPositions.remove(it.uniqueId)
            DungeonState.playerReturnGameModes.remove(it.uniqueId)
            DungeonState.playerParties.remove(it.uniqueId)
            DungeonState.playersTriggering.remove(it.uniqueId)
        }
        players.clear()
    }
}