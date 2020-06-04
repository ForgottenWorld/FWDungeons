package it.forgottenworld.dungeons.controller

import it.forgottenworld.dungeons.model.dungeon.Dungeon
import it.forgottenworld.dungeons.model.party.Party
import it.forgottenworld.dungeons.model.trigger.Trigger
import java.util.*

object FWDungeonsController {
    val dungeons = mutableMapOf<Int, Dungeon>()
    val activeDungeons = mutableMapOf<Int, Boolean>()
    val parties = mutableMapOf<Int, Party>()
    val playersTriggering = mutableMapOf<UUID, Trigger>()

    fun getDungeonById(id: Int) = dungeons[id]
    fun getMaxDungeonId() = dungeons.keys.max() ?: -1
}