package it.forgottenworld.dungeons.core.integrations

import com.google.inject.Inject
import com.google.inject.Singleton
import it.forgottenworld.dungeons.core.storage.Configuration
import it.forgottenworld.dungeons.core.storage.Strings
import it.forgottenworld.dungeons.core.utils.sendConsoleMessage
import net.milkbowl.vault.economy.Economy
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.ServicesManager

@Singleton
class VaultUtils @Inject constructor(
    private val configuration: Configuration,
    private val servicesManager: ServicesManager
) {

    private var useVault = false

    fun checkVaultIntegration() {
        sendConsoleMessage("${Strings.CONSOLE_PREFIX}Checking for Vault integration...")
        if (!configuration.vaultIntegration) {
            sendConsoleMessage(" -- Vault integration is §4not enabled")
            return
        }

        sendConsoleMessage(" -- Vault integration §2is enabled")
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            sendConsoleMessage(" -- Vault is §4not present")
            return
        }

        sendConsoleMessage(" -- Vault §2is present")
        useVault = true
    }

    fun canPlayerPay(player: Player, amount: Double): Boolean {
        if (!useVault) return true
        return servicesManager
            .getRegistration(Economy::class.java)
            ?.provider
            ?.has(
                Bukkit.getOfflinePlayer(player.uniqueId),
                amount
            ) == true
    }

    fun formatCurrency(amount: Double) = servicesManager
        .getRegistration(Economy::class.java)
        ?.provider
        ?.currencyNamePlural()
        ?.let { "$it $amount" }

    fun withdrawPlayer(player: Player, amount: Double) {
        if (!useVault) return
        servicesManager
            .getRegistration(Economy::class.java)
            ?.provider?.withdrawPlayer(Bukkit.getOfflinePlayer(player.uniqueId), amount)
    }
}