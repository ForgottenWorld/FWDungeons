package it.forgottenworld.dungeons.model.party

import it.forgottenworld.dungeons.model.dungeon.DungeonInstance
import org.bukkit.entity.Player

class Party(
        val id: Int,
        val players: List<Player> = listOf()
) {
    lateinit var instance: DungeonInstance
}