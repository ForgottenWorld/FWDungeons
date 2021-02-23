package it.forgottenworld.dungeons.core.game.unlocks

import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player
import java.util.*

object UnlockableManager {

    private val unlockableSeries = mutableMapOf<Int, UnlockableSeries>()
    private val playerUnlockProgress = mutableMapOf<Pair<UUID, Int>, Int>()

    fun loadUnlockablesFromConfig(config: ConfigurationSection) {
        for (key in config.getKeys(false)) {
            val series = UnlockableSeries.fromConfig(config.getConfigurationSection(key)!!)
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