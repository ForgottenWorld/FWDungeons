package it.forgottenworld.dungeons.api.game.instance

import org.bukkit.entity.Player

interface DungeonInstanceService {

    fun getInstanceByPlayer(player: Player): DungeonInstance?

    fun getInstanceById(id: Int): DungeonInstance?
}