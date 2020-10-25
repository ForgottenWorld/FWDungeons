package it.forgottenworld.dungeons.utils

import it.forgottenworld.dungeons.config.ConfigManager
import org.bukkit.Bukkit.getWorld
import org.bukkit.Location
import org.bukkit.Particle

fun repeatedlySpawnParticles(
        particle: Particle,
        locations: Set<Location>,
        count: Int,
        interval: Long,
        iterations: Int) {
    val world = getWorld(ConfigManager.dungeonWorld) ?: return
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
        controlVar: TypeWrapper<Boolean>,
        locationsGetter: () -> Set<Location>) {
    val world = getWorld(ConfigManager.dungeonWorld) ?: return
    bukkitThreadTimer(0L, interval) {
        //getLogger().info("sending particle at ${location.x} ${location.y} ${location.z}")
        locationsGetter().forEach {
            world.spawnParticle(
                    particle,
                    it.clone().add(0.5,0.5,0.5),
                    count)
        }
        if (!controlVar.value)
            cancel()
    }
}