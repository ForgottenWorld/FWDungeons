package it.forgottenworld.dungeons.model.trigger

import org.bukkit.entity.Player

class Trigger(
        val id: Int,
        val box: TriggerBox,
        val effect: (Player) -> Unit,
        val requiresWholeParty: Boolean = false) {

    val playersCurrentlyInside = listOf<Player>()
    var active = true

}