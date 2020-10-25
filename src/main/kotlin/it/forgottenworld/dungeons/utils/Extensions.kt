package it.forgottenworld.dungeons.utils

import it.forgottenworld.dungeons.FWDungeonsPlugin
import it.forgottenworld.dungeons.cli.Strings
import it.forgottenworld.dungeons.cli.getString
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.util.BlockVector
import org.bukkit.util.Vector
import java.util.*

val Player.targetBlock
        inline get() = getTargetBlock(null as Set<Material>?, 5)

val Block.blockVector
        inline get() = BlockVector(x, y, z)

fun Player.idEquals(player: Player) =
        this.uniqueId == player.uniqueId

fun Iterable<Player>.findPlayerById(player: Player) =
        this.find { it.idEquals(player) }

fun Iterable<Player>.findPlayerById(uuid: UUID) =
        this.find { it.uniqueId == uuid }

fun BlockVector.toVector() =
        Vector(this.x, this.y, this.z)

fun BlockVector.withRefSystemOrigin(oldOrigin: BlockVector, newOrigin: BlockVector) =
        BlockVector(
                x - oldOrigin.x + newOrigin.x,
                y - oldOrigin.y + newOrigin.y,
                z - oldOrigin.z + newOrigin.z)

inline fun bukkitThreadAsync(crossinline action: BukkitRunnable.() -> Unit) =
        object: BukkitRunnable() {
                override fun run() = action()
        }.runTaskAsynchronously(FWDungeonsPlugin.instance)

inline fun bukkitThreadLater(delay: Long, crossinline action: BukkitRunnable.() -> Unit) =
        object: BukkitRunnable() {
                override fun run() = action()
        }.runTaskLater(FWDungeonsPlugin.instance, delay)

inline fun bukkitThreadTimer(delay: Long, interval: Long, crossinline action: BukkitRunnable.() -> Unit) =
        object: BukkitRunnable() {
            override fun run() = action()
        }.runTaskTimer(FWDungeonsPlugin.instance, delay, interval)

fun CommandSender.sendFWDMessage(message: String) =
        sendMessage("${getString(Strings.CHAT_PREFIX)}$message")