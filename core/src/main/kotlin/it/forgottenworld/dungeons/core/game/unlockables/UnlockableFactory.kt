package it.forgottenworld.dungeons.core.game.unlockables

import com.google.inject.assistedinject.Assisted
import it.forgottenworld.dungeons.api.game.unlockables.Unlockable

interface UnlockableFactory {
    fun create(
        @Assisted("seriesId") seriesId: Int,
        @Assisted("order") order: Int,
        @Assisted("message") message: String,
        @Assisted("unlockedMessage") unlockedMessage: String,
        requirements: List<Unlockable.UnlockableRequirement>
    ) : Unlockable
}