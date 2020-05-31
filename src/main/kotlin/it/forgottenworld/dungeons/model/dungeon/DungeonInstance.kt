package it.forgottenworld.dungeons.model.dungeon

import it.forgottenworld.dungeons.model.party.Party
import it.forgottenworld.dungeons.model.trigger.Trigger

class DungeonInstance(
        val id: Int,
        val dungeon: Dungeon,
        val box: DungeonBox,
        val triggers: List<Trigger>) {
    var party: Party? = null

    fun isInstanceBusy() = party != null

    fun startInstance(party: Party) {
        this.party = party
    }

}