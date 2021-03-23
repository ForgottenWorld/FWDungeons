package it.forgottenworld.dungeons.api.game.interactiveregion

import it.forgottenworld.dungeons.api.math.Box

interface InteractiveRegion {

    val id: Int

    var label: String?

    val box: Box

    enum class Type { TRIGGER, ACTIVE_AREA, SPAWN_AREA }
}