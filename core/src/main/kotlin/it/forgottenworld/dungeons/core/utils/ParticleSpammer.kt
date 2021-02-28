package it.forgottenworld.dungeons.core.utils

import it.forgottenworld.dungeons.api.math.Box
import it.forgottenworld.dungeons.api.math.Vector3i
import it.forgottenworld.dungeons.core.config.Configuration
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import org.bukkit.Location
import org.bukkit.Particle

class ParticleSpammer(
    private val particle: Particle,
    private val count: Int,
    private val interval: Long,
    private val locations: List<Vector3i>
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
                val world = Configuration.dungeonWorld
                locations.forEach {
                    world.spawnParticle(
                        particle,
                        Location(
                            Configuration.dungeonWorld,
                            it.x + 0.5,
                            it.y + 0.5,
                            it.z + 0.5
                        ),
                        count
                    )
                }
            }
        }
    }

    companion object {

        fun highlightBox(box: Box) {
            repeatedlySpawnParticles(
                Particle.COMPOSTER,
                box.getAllBlocks(Configuration.dungeonWorld).map { it.location },
                1,
                500,
                20
            )
        }

        fun repeatedlySpawnParticles(
            particle: Particle,
            locations: Iterable<Location>,
            count: Int,
            interval: Long,
            iterations: Int
        ) {
            val world = Configuration.dungeonWorld
            launch {
                for (i in 0 until iterations) {
                    delay(interval)
                    locations.forEach {
                        world.spawnParticle(
                            particle,
                            it.clone().add(0.5, 0.5, 0.5),
                            count
                        )
                    }
                }
            }
        }
    }
}