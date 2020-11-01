package it.forgottenworld.dungeons.utils

import it.forgottenworld.dungeons.config.ConfigManager
import kotlinx.coroutines.delay
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.util.BlockVector

fun repeatedlySpawnParticles(
        particle: Particle,
        locations: Set<Location>,
        count: Int,
        interval: Long,
        iterations: Int) {
    val world = ConfigManager.dungeonWorld
    launch {
        for (i in 0 until iterations) {
            delay(interval)
            locations.forEach {
                world.spawnParticle(
                        particle,
                        it.clone().add(0.5, 0.5, 0.5),
                        count)
            }
        }
    }
}

class ParticleSpammer(
        particle: Particle,
        count: Int,
        interval: Long,
        getLocations: () -> Set<BlockVector>) {

    private var doRun = true

    fun stop() {
        doRun = false
    }

    init {
        launch {
            while (doRun) {
                delay(interval)
                val world = ConfigManager.dungeonWorld
                getLocations().forEach {
                    world.spawnParticle(
                            particle,
                            Location(ConfigManager.dungeonWorld, it.x + 0.5, it.y + 0.5, it.z + 0.5),
                            count)
                }
            }
        }
    }

}