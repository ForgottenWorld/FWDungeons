package it.forgottenworld.dungeons.core.game.unlockables

import com.google.inject.Inject
import com.google.inject.Singleton
import it.forgottenworld.dungeons.api.game.unlockables.Unlockable
import it.forgottenworld.dungeons.api.game.unlockables.UnlockableSeries
import it.forgottenworld.dungeons.api.storage.Storage
import it.forgottenworld.dungeons.api.storage.Storage.Companion.load
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player
import java.util.*

@Singleton
class UnlockableManager @Inject constructor(
    private val storage: Storage
) {

    private val unlockableSeries = mutableMapOf<Int, UnlockableSeries>()
    private val playerUnlockProgress = mutableMapOf<Pair<UUID, Int>, Int>()

    fun loadUnlockablesFromConfig(config: ConfigurationSection) {
        for (key in config.getKeys(false)) {
            val series = storage.load<UnlockableSeries>(config.getConfigurationSection(key)!!)
            unlockableSeries[series.id] = series
        }
    }

    fun hasPlayerUnlocked(player: Player, seriesId: Int, unlockableOrder: Int) =
        playerUnlockProgress[player.uniqueId to seriesId] ?: 0 >= unlockableOrder

    fun tryPlayerUnlock(player: Player, unlockable: Unlockable): Boolean {
        val key = player.uniqueId to unlockable.seriesId
        val progress = playerUnlockProgress[key] ?: run {
            playerUnlockProgress[key] = 0
            0
        }
        if (progress == unlockable.order) {
            playerUnlockProgress[key] = progress + 1
            return true
        }
        return false
    }
}