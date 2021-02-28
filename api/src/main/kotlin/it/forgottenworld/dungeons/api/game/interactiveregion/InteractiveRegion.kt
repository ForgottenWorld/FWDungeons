package it.forgottenworld.dungeons.api.game.interactiveregion

import it.forgottenworld.dungeons.api.math.Box
import it.forgottenworld.dungeons.api.math.Vector3i

interface InteractiveRegion {
    val id: Int
    val box: Box

    fun withContainerOrigin(oldOrigin: Vector3i, newOrigin: Vector3i): InteractiveRegion
    fun withContainerOriginZero(oldOrigin: Vector3i): InteractiveRegion

    enum class Type { TRIGGER, ACTIVE_AREA }
}