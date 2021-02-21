package it.forgottenworld.dungeons.core.utils

import io.lumine.xikage.mythicmobs.api.bukkit.BukkitAPIHelper
import it.forgottenworld.dungeons.api.math.Box
import it.forgottenworld.dungeons.api.math.Vector3i
import it.forgottenworld.dungeons.core.FWDungeonsPlugin
import it.forgottenworld.dungeons.core.config.ConfigManager
import it.forgottenworld.dungeons.core.config.Strings
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.block.Block
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

val Player.targetBlock
    get() = getTargetBlock(null as Set<Material>?, 5)

val Block.vector3i
    get() = Vector3i(x, y, z)

fun Location.toVector3i() = Vector3i(blockX, blockY, blockZ)

fun Vector3i.cubeWithSide(side: Int) = Box(
    this,
    side,
    side,
    side
)

fun CommandSender.sendFWDMessage(message: String) = sendMessage("${Strings.CHAT_PREFIX}$message")

fun Iterable<Int>.firstGap() = sorted().find { !contains(it + 1) }?.plus(1) ?: 0

val plugin get() = JavaPlugin.getPlugin(FWDungeonsPlugin::class.java)

val mythicMobsHelper by lazy { BukkitAPIHelper() }

val dungeonWorld get() = ConfigManager.dungeonWorld

fun Box.highlightAll() {
    ParticleSpammer.repeatedlySpawnParticles(
        Particle.COMPOSTER,
        getAllBlocks(dungeonWorld).map { it.location },
        1,
        500,
        20
    )
}