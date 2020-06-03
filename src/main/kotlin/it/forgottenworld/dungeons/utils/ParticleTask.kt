package it.forgottenworld.dungeons.utils

import it.forgottenworld.dungeons.FWDungeonsPlugin
import it.forgottenworld.dungeons.config.ConfigManager
import org.bukkit.Bukkit.getWorld
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.scheduler.BukkitRunnable

class ParticleTask(private val action : () -> Unit) : BukkitRunnable() {
    override fun run() {
        action()
    }
}


fun repeatedlySpawnParticles(
        particle: Particle,
        location: Location,
        count: Int,
        interval: Long,
        iterations: Int) {
    val world = getWorld(ConfigManager.dungeonWorld) ?: return
    for (i in 0 until iterations) {
        ParticleTask {
            world.apply {
                spawnParticle(
                        particle,
                        location,
                        count)
            }
        }.runTaskLaterAsynchronously(FWDungeonsPlugin.instance, interval * i)
    }
}