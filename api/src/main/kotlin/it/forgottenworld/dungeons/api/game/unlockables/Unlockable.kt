package it.forgottenworld.dungeons.api.game.unlockables

import it.forgottenworld.dungeons.api.storage.Storage
import org.bukkit.Material
import org.bukkit.entity.Player

interface Unlockable : Storage.Storable {
    val seriesId: Int
    val order: Int
    val message: String
    val unlockedMessage: String
    val requirements: List<Requirement>

    sealed class Requirement {
        class Item(
            val material: Material,
            val amount: Int
        ): Requirement()

        class Economy(
            val amount: Double
        ): Requirement()
    }

    fun verifyPlayerRequirements(player: Player): Boolean

    fun printRequirements(): String

    fun executeRequirements(player: Player): Boolean
}