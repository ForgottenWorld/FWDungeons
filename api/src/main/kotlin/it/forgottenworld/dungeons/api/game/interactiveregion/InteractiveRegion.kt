package it.forgottenworld.dungeons.api.game.interactiveregion

import it.forgottenworld.dungeons.api.math.Box

interface InteractiveRegion {
    val id: Int
    val box: Box

    enum class Type { TRIGGER, ACTIVE_AREA }
}