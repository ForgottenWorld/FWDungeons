package it.forgottenworld.dungeons.core.game.unlockables

import it.forgottenworld.dungeons.api.game.unlockables.Unlockable

interface UnlockableFactory {
    fun create(
        seriesId: Int,
        order: Int,
        message: String,
        unlockedMessage: String,
        requirements: List<Unlockable.UnlockableRequirement>
    ) : Unlockable
}