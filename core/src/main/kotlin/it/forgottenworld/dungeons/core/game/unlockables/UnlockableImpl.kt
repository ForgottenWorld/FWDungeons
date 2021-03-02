package it.forgottenworld.dungeons.core.game.unlockables

import com.google.inject.Inject
import com.google.inject.assistedinject.Assisted
import it.forgottenworld.dungeons.api.game.unlockables.Unlockable
import it.forgottenworld.dungeons.api.storage.Storage
import it.forgottenworld.dungeons.core.integrations.VaultUtils
import org.bukkit.entity.Player

data class UnlockableImpl @Inject constructor(
    @Assisted("seriesId") override val seriesId: Int,
    @Assisted("order") override val order: Int,
    @Assisted("message") override val message: String,
    @Assisted("unlockedMessage") override val unlockedMessage: String,
    @Assisted override val requirements: List<Unlockable.UnlockableRequirement>,
    private val vaultUtils: VaultUtils
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
                    if (!vaultUtils.canPlayerPay(player, req.amount)) {
                        return false
                    }
                }
            }
        }
        return true
    }
}