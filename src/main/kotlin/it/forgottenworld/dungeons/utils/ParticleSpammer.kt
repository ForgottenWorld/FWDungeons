package it.forgottenworld.dungeons.utils

import it.forgottenworld.dungeons.config.ConfigManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.util.BlockVector

class ParticleSpammer(
    private val particle: Particle,
    private val count: Int,
    private val interval: Long,
    private val locations: List<BlockVector>
) {

    private var job: Job? = null

    fun stop() {
        job?.cancel()
        job = null
    }

    init {
        job = launch {
            while (true) {
                delay(interval)
                val world = ConfigManager.dungeonWorld
                locations.forEach {
                    world.spawnParticle(
                        particle,
                        Location(ConfigManager.dungeonWorld, it.x + 0.5, it.y + 0.5, it.z + 0.5),
                        count
                    )
                }
            }
        }
    }

    companion object {

        fun repeatedlySpawnParticles(
            particle: Particle,
            locations: Iterable<Location>,
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
    }
}