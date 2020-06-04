package it.forgottenworld.dungeons.model.party

import it.forgottenworld.dungeons.model.dungeon.DungeonInstance
import it.forgottenworld.dungeons.utils.findPlayerById
import org.bukkit.entity.Player

class Party(
        val id: Int,
        val players: MutableList<Player> = mutableListOf(),
        val leader: Player,
        val instance: DungeonInstance
) {
    var inGame = false

    fun startInstance(instance: DungeonInstance) {
        instance.startInstance(this)
        inGame = true
    }

    fun playerCount() = players.count()

    fun playerJoin(player: Player) {
        players.findPlayerById(player.uniqueId)
            ?: players.add(player)
    }

    fun playerLeave(player: Player) {
        players.removeIf {
            it.uniqueId == player.uniqueId
        }
    }
}