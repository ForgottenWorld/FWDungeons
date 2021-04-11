package it.forgottenworld.dungeons.core.game.dungeon.subelement.interactiveregion.trigger

import it.forgottenworld.dungeons.api.game.dungeon.subelement.interactiveregion.Trigger
import it.forgottenworld.dungeons.api.math.Box

interface TriggerFactory {
    fun create(
        id: Int,
        box: Box,
        requiresWholeParty: Boolean = false,
        label: String? = null
    ): Trigger
}