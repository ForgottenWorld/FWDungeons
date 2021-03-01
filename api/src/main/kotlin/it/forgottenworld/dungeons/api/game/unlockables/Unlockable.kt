package it.forgottenworld.dungeons.api.game.unlockables

import it.forgottenworld.dungeons.api.storage.Storage
import org.bukkit.Material

interface Unlockable : Storage.Storable {
    val seriesId: Int
    val order: Int
    val message: String
    val unlockedMessage: String
    val requirements: List<UnlockableRequirement>

    interface UnlockableRequirement

    class ItemRequirement(
        val material: Material,
        val amount: Int
    ): UnlockableRequirement

    class EconomyRequirement(
        val amount: Double
    ): UnlockableRequirement
}