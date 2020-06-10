package it.forgottenworld.dungeons.model.party

import it.forgottenworld.dungeons.controller.FWDungeonsController
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
            players.forEach { it.sendMessage("${player.name} joined the dungeon party") }
            FWDungeonsController.playerParties[player.uniqueId] = this
            players.add(player)
        }
    }

    fun playerLeave(player: Player) {
        players.remove(player)
        FWDungeonsController.playerReturnPositions.remove(player.uniqueId)
        FWDungeonsController.playerParties.remove(player.uniqueId)
        FWDungeonsController.playersTriggering[player.uniqueId]?.onPlayerExit(player)
        FWDungeonsController.playersTriggering.remove(player.uniqueId)
        players.forEach { it.sendMessage("${player.name} left the dungeon party") }
        if (leader == player) {
            if (players.isEmpty()) {
                instance.resetInstance()
            } else {
                leader = players.first().apply { sendMessage("You're now the party leader") }
            }
        }
    }

    fun playerDied(player: Player) {
        players.remove(player)
        FWDungeonsController.playerReturnPositions.remove(player.uniqueId)
        FWDungeonsController.playerParties.remove(player.uniqueId)
        FWDungeonsController.playersTriggering[player.uniqueId]?.onPlayerExit(player)
        FWDungeonsController.playersTriggering.remove(player.uniqueId)
        players.forEach { it.sendMessage("${player.name} died in the dungeon") }
        if (leader == player) {
            if (players.isEmpty()) {
                instance.resetInstance()
            } else {
                leader = players.first().apply { sendMessage("You're now the party leader") }
            }
        }
    }

    fun disband() {
        players.forEach {
            FWDungeonsController.playerReturnPositions.remove(it.uniqueId)
            FWDungeonsController.playerParties.remove(it.uniqueId)
            FWDungeonsController.playersTriggering.remove(it.uniqueId)
        }
        players.clear()
    }
}