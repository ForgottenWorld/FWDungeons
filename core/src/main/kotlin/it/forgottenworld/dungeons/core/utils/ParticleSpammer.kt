package it.forgottenworld.dungeons.core.utils

import it.forgottenworld.dungeons.api.math.Vector3d
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import org.bukkit.Particle
import org.bukkit.World
import java.lang.ref.WeakReference

class ParticleSpammer private constructor(
    private val particle: Particle,
    private val count: Int,
    private val interval: Long,
    private val locations: List<Vector3d>
) {

    private var job: Job? = null

    private fun start(world: World) {
        job = launch {
            while (true) {
                locations.forEach {
                    world.spawnParticle(particle, it.x, it.y, it.z, count)
                }
                delay(interval)
            }
        }
    }

    fun stop() {
        job?.cancel()
        job = null
    }


    class Builder {

        private lateinit var particle: Particle

        private var count: Int = 1

        private var interval = 500L

        private lateinit var locations: List<Vector3d>

        private lateinit var world: WeakReference<World>


        fun particle(particle: Particle): Builder {
            this.particle = particle
            return this
        }

        fun count(count: Int): Builder {
            this.count = count
            return this
        }

        fun interval(interval: Long): Builder {
            this.interval = interval
            return this
        }

        fun locations(locations: List<Vector3d>): Builder {
            this.locations = locations
            return this
        }

        fun world(world: World): Builder {
            this.world = WeakReference(world)
            return this
        }

        fun build(): ParticleSpammer {
            val world = world.get()!!
            this.world.clear()
            val spammer = ParticleSpammer(particle, count, interval, locations)
            spammer.start(world)
            return spammer
        }

        fun oneShot(iterations: Int) {
            val world = world.get()!!
            this.world.clear()
            launch {
                repeat(iterations) {
                    locations.forEach {
                        world.spawnParticle(particle, it.x, it.y, it.z, count)
                    }
                    delay(interval)
                }
            }
        }
    }


    companion object {

        fun builder() = Builder()
    }
}