package it.forgottenworld.dungeons.core.game.unlockables

import it.forgottenworld.dungeons.api.game.unlockables.Unlockable
import it.forgottenworld.dungeons.core.config.Storage
import it.forgottenworld.dungeons.core.integrations.VaultUtils
import org.bukkit.entity.Player

data class UnlockableImpl(
    override val seriesId: Int,
    override val order: Int,
    override val message: String,
    override val unlockedMessage: String,
    override val requirements: List<Unlockable.UnlockableRequirement>
) : Unlockable, Storage.Storable {

    fun verifyPlayerRequirements(player: Player): Boolean {
        for(req in requirements) {
            when (req) {
                is Unlockable.ItemRequirement -> {
                    if (!player.inventory.contains(req.material, req.amount)) {
                        return false
                    }
                }
                is Unlockable.EconomyRequirement -> {
                    if (!VaultUtils.canPlayerPay(player, req.amount)) {
                        return false
                    }
                }
            }
        }
        return true
    }
}