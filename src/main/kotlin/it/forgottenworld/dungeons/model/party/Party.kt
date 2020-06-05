package it.forgottenworld.dungeons.model.party

import it.forgottenworld.dungeons.controller.FWDungeonsController
import it.forgottenworld.dungeons.model.dungeon.DungeonInstance
import it.forgottenworld.dungeons.utils.findPlayerById
import it.forgottenworld.dungeons.utils.getRandomString
import org.bukkit.entity.Player

class Party(
        val id: Int,
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
        FWDungeonsController.playerParties.remove(player.uniqueId)
        players.forEach { it.sendMessage("${player.name} left the dungeon party") }
        if (leader == player) {
            if (players.isEmpty()) {
                instance.resetInstance()
                FWDungeonsController.parties.remove(id)
            } else {
                leader = players.first().apply { sendMessage("You're now the party leader") }
            }
        }
    }
}