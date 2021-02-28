package it.forgottenworld.dungeons.core.game.unlockables

import it.forgottenworld.dungeons.api.game.unlockables.Unlockable
import it.forgottenworld.dungeons.api.game.unlockables.UnlockableSeries
import it.forgottenworld.dungeons.core.config.Storage

data class UnlockableSeriesImpl(
    override val id: Int,
    override val name: String,
    override val description: String,
    override val unlockables: List<Unlockable>
) : UnlockableSeries, Storage.Storable {

}