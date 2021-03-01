package it.forgottenworld.dungeons.core.integrations

import com.google.inject.Inject
import it.forgottenworld.dungeons.core.FWDungeonsPlugin
import it.forgottenworld.dungeons.core.config.Configuration
import net.milkbowl.vault.economy.Economy
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class VaultUtils @Inject constructor(
    private val plugin: FWDungeonsPlugin,
    private val configuration: Configuration
) {

    private val economy get() = plugin
        .server
        .servicesManager
        .getRegistration(Economy::class.java)
        ?.provider

    fun checkVaultIntegration() {
        val logger = Bukkit.getLogger()
        logger.info("Checking for Vault integration...")
        if (!configuration.vaultIntegration) {
            logger.info("Vault integration is not enabled")
            return
        }

        logger.info("Vault integration is enabled")
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            logger.info("Vault is not present")
            return
        }

        logger.info("Vault is present")
        configuration.useEasyRanking = true
    }

    fun canPlayerPay(player: Player, amount: Double): Boolean {
        if (!configuration.useVault) return true
        return economy?.has(
            Bukkit.getOfflinePlayer(player.uniqueId),
            amount
        ) == true
    }

    fun withdrawPlayer(player: Player, amount: Double) {
        if (!configuration.useVault) return
        economy?.withdrawPlayer(Bukkit.getOfflinePlayer(player.uniqueId), amount)
    }
}