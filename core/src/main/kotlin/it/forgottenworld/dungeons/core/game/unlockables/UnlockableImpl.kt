package it.forgottenworld.dungeons.core.game.unlockables

import it.forgottenworld.dungeons.api.game.unlockables.Unlockable
import it.forgottenworld.dungeons.core.integrations.VaultUtils
import org.bukkit.Material
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player

data class UnlockableImpl(
    override val seriesId: Int,
    override val order: Int,
    override val message: String,
    override val unlockedMessage: String,
    override val requirements: List<Unlockable.UnlockableRequirement>
) : Unlockable {


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

    fun toConfig(config: ConfigurationSection) {
        config.set("seriesId", seriesId)
        config.set("order", order)
        config.set("message", message)
        config.set("unlockedMessage", unlockedMessage)
        config.createSection("requirements").run {
            for (req in requirements) {
                when (req) {
                    is Unlockable.EconomyRequirement -> {
                        set("CURRENCY", req.amount)
                    }
                    is Unlockable.ItemRequirement -> {
                        set(req.material.toString(), req.amount)
                    }
                }
            }
        }
    }

    companion object {

        fun fromConfig(config: ConfigurationSection) = UnlockableImpl(
            config.getInt("seriesId"),
            config.getInt("order"),
            config.getString("message")!!,
            config.getString("unlockedMessage")!!,
            config.getConfigurationSection("requirements")!!.run {
                getKeys(false).map { rk ->
                    if (rk == "CURRENCY") {
                        Unlockable.EconomyRequirement(getDouble(rk))
                    } else {
                        Unlockable.ItemRequirement(Material.getMaterial(rk)!!, getInt(rk))
                    }
                }
            }
        )
    }
}