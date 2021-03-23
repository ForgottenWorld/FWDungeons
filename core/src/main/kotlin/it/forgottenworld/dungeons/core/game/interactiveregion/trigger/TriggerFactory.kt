package it.forgottenworld.dungeons.core.game.interactiveregion.trigger

import it.forgottenworld.dungeons.api.game.interactiveregion.Trigger
import it.forgottenworld.dungeons.api.math.Box

interface TriggerFactory {
    fun create(
        id: Int,
        box: Box,
        requiresWholeParty: Boolean = false,
        label: String? = null
    ): Trigger
}