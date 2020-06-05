package it.forgottenworld.dungeons.utils

import it.forgottenworld.dungeons.FWDungeonsPlugin
import it.forgottenworld.dungeons.config.ConfigManager
import org.bukkit.Bukkit.*
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.scheduler.BukkitRunnable

fun repeatedlySpawnParticles(
        particle: Particle,
        locations: Set<Location>,
        count: Int,
        interval: Long,
        iterations: Int) {
    val world = getWorld(ConfigManager.dungeonWorld) ?: return
    object : BukkitRunnable() {
        var i = 0
        override fun run() {
            //getLogger().info("sending particle at ${location.x} ${location.y} ${location.z}")
            locations.forEach {
                world.spawnParticle(
                        particle,
                        it.clone().add(0.5,0.5,0.5),
                        count)
            }
            if (++i == iterations)
                cancel()
        }
    }.runTaskTimer(FWDungeonsPlugin.instance, 0L, interval)
}