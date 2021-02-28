package it.forgottenworld.dungeons.core.integrations

import it.forgottenworld.dungeons.core.FWDungeonsPlugin
import it.forgottenworld.dungeons.core.config.Configuration
import net.milkbowl.vault.economy.Economy
import org.bukkit.Bukkit
import org.bukkit.entity.Player

object VaultUtils {

    private val economy get() = FWDungeonsPlugin.getInstance()
        .server
        .servicesManager
        .getRegistration(Economy::class.java)
        ?.provider

    fun checkVaultIntegration() {
        val logger = Bukkit.getLogger()
        logger.info("Checking for Vault integration...")
        if (!Configuration.vaultIntegration) {
            logger.info("Vault integration is not enabled")
            return
        }

        logger.info("Vault integration is enabled")
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            logger.info("Vault is not present")
            return
        }

        logger.info("Vault is present")
        Configuration.useEasyRanking = true
    }

    fun canPlayerPay(player: Player, amount: Double): Boolean {
        if (!Configuration.useVault) return true
        return economy?.has(
            Bukkit.getOfflinePlayer(player.uniqueId),
            amount
        ) == true
    }

    fun playerPay(player: Player, amount: Double) {
        if (!Configuration.useVault) return
        economy?.withdrawPlayer(Bukkit.getOfflinePlayer(player.uniqueId), amount)
    }
}