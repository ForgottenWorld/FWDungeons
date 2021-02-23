package it.forgottenworld.dungeons.core.game.unlocks

import it.forgottenworld.dungeons.core.integrations.VaultUtils
import org.bukkit.Material
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player

data class Unlockable(
    val seriesId: Int,
    val order: Int,
    val message: String,
    val unlockedMessage: String,
    val requirements: List<UnlockableRequirement>
) {
    interface UnlockableRequirement

    class ItemRequirement(
        val material: Material,
        val amount: Int
    ): UnlockableRequirement

    class EconomyRequirement(
        val amount: Double
    ): UnlockableRequirement

    fun verifyPlayerRequirements(player: Player): Boolean {
        for(req in requirements) {
            when (req) {
                is ItemRequirement -> {
                    if (!player.inventory.contains(req.material, req.amount)) {
                        return false
                    }
                }
                is EconomyRequirement -> {
                    if (!VaultUtils.canPlayerPay(player, req.amount)) {
                        return false
                    }
                }
            }
        }
        return true
    }

    companion object {

        fun fromConfig(config: ConfigurationSection) = Unlockable(
            config.getInt("seriesId"),
            config.getInt("order"),
            config.getString("message")!!,
            config.getString("unlockedMessage")!!,
            config.getConfigurationSection("requirements")!!.run {
                getKeys(false).map { rk ->
                    if (rk == "CURRENCY") {
                        EconomyRequirement(getDouble(rk))
                    } else {
                        ItemRequirement(Material.getMaterial(rk)!!, getInt(rk))
                    }
                }
            }
        )
    }
}