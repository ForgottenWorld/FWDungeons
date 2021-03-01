package it.forgottenworld.dungeons.core.utils

import it.forgottenworld.dungeons.api.math.Box
import it.forgottenworld.dungeons.api.math.Vector3d
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import org.bukkit.Particle
import org.bukkit.World

class ParticleSpammer(
    private val particle: Particle,
    private val count: Int,
    private val interval: Long,
    private val locations: List<Vector3d>,
    world: World
) {

    private var job: Job? = null

    fun stop() {
        job?.cancel()
        job = null
    }

    init {
        start(world)
    }

    private fun start(world: World) {
        job = launch {
            while (true) {
                delay(interval)
                locations.forEach {
                    world.spawnParticle(
                        particle,
                        it.x + 0.5,
                        it.y + 0.5,
                        it.z + 0.5,
                        count
                    )
                }
            }
        }
    }

    companion object {

        fun highlightBox(box: Box, world: World) {
            repeatedlySpawnParticles(
                Particle.COMPOSTER,
                box.getCenterOfAllBlocks(),
                20,
                world
            )
        }

        fun activeAreaSwirls(box: Box, world: World) {
            repeatedlySpawnParticles(
                Particle.PORTAL,
                box.getCenterOfAllBlocks(),
                4,
                world
            )
        }

        private fun repeatedlySpawnParticles(
            particle: Particle,
            locations: Iterable<Vector3d>,
            iterations: Int,
            world: World
        ) {
            launch {
                for (i in 0 until iterations) {
                    delay(500)
                    locations.forEach {
                        world.spawnParticle(particle, it.x, it.y, it.z, 1)
                    }
                }
            }
        }
    }
}