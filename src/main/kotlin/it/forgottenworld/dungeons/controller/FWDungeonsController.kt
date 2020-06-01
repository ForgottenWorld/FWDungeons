package it.forgottenworld.dungeons.controller

import it.forgottenworld.dungeons.model.dungeon.Dungeon

object FWDungeonsController {
    val dungeons = listOf<Dungeon>()

    fun getDungeonById(id: Int) = dungeons.find { it.id == id }

    fun getMaxDungeonId() = dungeons.maxBy { it.id }?.id
}