package it.forgottenworld.dungeons.model.trigger

import it.forgottenworld.dungeons.model.box.Box
import org.bukkit.entity.Player

class Trigger(
        val id: Int,
        val box: Box,
        val effect: (Player) -> Unit,
        val requiresWholeParty: Boolean = false) {

    val playersCurrentlyInside = listOf<Player>()
    var active = true

}