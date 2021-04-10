package it.forgottenworld.dungeons.api.game.dungeon.subelement.interactiveregion

import it.forgottenworld.dungeons.api.game.dungeon.subelement.DungeonSubElement
import it.forgottenworld.dungeons.api.math.Box

interface InteractiveRegion : DungeonSubElement {

    val box: Box

    enum class Type { TRIGGER, ACTIVE_AREA, SPAWN_AREA }
}