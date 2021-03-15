package it.forgottenworld.dungeons.api.game.unlockables

import it.forgottenworld.dungeons.api.storage.Storage

interface UnlockableSeries : Storage.Storable {
    val id: Int
    val name: String
    val description: String
    val unlockables: List<Unlockable>
}