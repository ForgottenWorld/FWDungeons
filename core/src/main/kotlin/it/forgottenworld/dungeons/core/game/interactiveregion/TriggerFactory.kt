package it.forgottenworld.dungeons.core.game.interactiveregion

import it.forgottenworld.dungeons.api.game.interactiveregion.Trigger
import it.forgottenworld.dungeons.api.math.Box

interface TriggerFactory {
    fun create(
        id: Int,
        box: Box,
        effectCode: List<String> = listOf(),
        requiresWholeParty: Boolean = false,
        label: String? = null
    ): Trigger
}