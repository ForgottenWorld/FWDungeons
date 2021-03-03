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
    @Assisted override val requirements: List<Unlockable.Requirement>,
    private val vaultUtils: VaultUtils
) : Unlockable, Storage.Storable {

    override fun printRequirements() = requirements.joinToString("\n") {
        val reqStr = when (it) {
            is Unlockable.Requirement.Item -> "${it.material} x ${it.amount}"
            is Unlockable.Requirement.Economy -> vaultUtils.formatCurrency(it.amount)
        }
        " - $reqStr"
    }

    override fun executeRequirements(player: Player): Boolean {
        if (!verifyPlayerRequirements(player)) return false
        for(req in requirements) {
            when (req) {
                is Unlockable.Requirement.Item -> {
                    var toRemove = req.amount
                    for (stack in player.inventory.all(req.material).values) {
                        if (stack.amount > toRemove) {
                            stack.amount -= toRemove
                            break
                        }
                        toRemove -= stack.amount
                        player.inventory.remove(stack)
                        if (toRemove == 0) break
                    }
                }
                is Unlockable.Requirement.Economy -> {
                    vaultUtils.withdrawPlayer(player, req.amount)
                }
            }
        }
        return true
    }

    override fun verifyPlayerRequirements(player: Player): Boolean {
        for(req in requirements) {
            when (req) {
                is Unlockable.Requirement.Item -> {
                    if (!player.inventory.contains(req.material, req.amount)) {
                        return false
                    }
                }
                is Unlockable.Requirement.Economy -> {
                    if (!vaultUtils.canPlayerPay(player, req.amount)) {
                        return false
                    }
                }
            }
        }
        return true
    }
}