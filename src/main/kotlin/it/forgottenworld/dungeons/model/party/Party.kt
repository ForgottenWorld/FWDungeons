package it.forgottenworld.dungeons.model.party

import it.forgottenworld.dungeons.model.dungeon.DungeonInstance
import it.forgottenworld.dungeons.utils.findPlayerById
import it.forgottenworld.dungeons.utils.getRandomString
import org.bukkit.entity.Player

class Party(
        val id: Int,
        val players: MutableList<Player> = mutableListOf(),
        var leader: Player,
        val maxPlayers: Int,
        val isLocked: Boolean,
        val instance: DungeonInstance
) {
    var inGame = false

    val playerCount: Int
        get() = players.count()
    val isFull: Boolean
        get() = playerCount == maxPlayers


    fun playerJoin(player: Player) : Boolean {
        if (players.count() == maxPlayers) return false
        return if (players.contains(player)) false
            else players.add(player)
    }

    val joinKey = if (isLocked) getRandomString(10) else ""

    fun playerLeave(player: Player) {
        players.remove(player)
        if (leader == player)
            if (players.isEmpty())
                instance.resetInstance()
            else
                leader = players.first()
    }
}