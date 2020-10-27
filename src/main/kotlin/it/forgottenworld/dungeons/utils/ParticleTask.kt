package it.forgottenworld.dungeons.utils

import it.forgottenworld.dungeons.config.ConfigManager
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.scheduler.BukkitTask

fun repeatedlySpawnParticles(
        particle: Particle,
        locations: Set<Location>,
        count: Int,
        interval: Long,
        iterations: Int) {
    val world = ConfigManager.dungeonWorld
    var i = 0
    bukkitThreadTimer(0L, interval) {
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
}

fun repeatedlySpawnParticles(
        particle: Particle,
        count: Int,
        interval: Long,
        getLocations: () -> Set<Location>): BukkitTask {
    val world = ConfigManager.dungeonWorld
    return bukkitThreadTimer(0L, interval) {
        getLocations().forEach {
            world.spawnParticle(
                    particle,
                    Location(it.world, it.x + 0.5, it.y + 0.5, it.z + 0.5),
                    count)
        }
    }
}