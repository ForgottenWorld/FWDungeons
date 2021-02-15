package it.forgottenworld.dungeons.game.interactiveregion

import it.forgottenworld.dungeons.game.box.Box
import it.forgottenworld.dungeons.utils.Vector3i

interface InteractiveRegion {
    val id: Int
    val box: Box

    fun withContainerOrigin(oldOrigin: Vector3i, newOrigin: Vector3i): InteractiveRegion

    enum class Type { TRIGGER, ACTIVE_AREA }
}