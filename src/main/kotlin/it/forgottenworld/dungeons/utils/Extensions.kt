package it.forgottenworld.dungeons.utils

import it.forgottenworld.dungeons.FWDungeonsPlugin
import it.forgottenworld.dungeons.cli.Strings
import it.forgottenworld.dungeons.cli.getString
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.block.Block
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.util.BlockVector
import org.bukkit.util.Vector
import java.util.*
import kotlin.math.max
import kotlin.math.min

val Player.targetBlock
        inline get() = getTargetBlock(null as Set<Material>?, 5)

val Block.blockVector
        inline get() = BlockVector(x, y, z)

fun Iterable<Player>.findPlayerById(player: Player) = find { it.uniqueId == player.uniqueId }

fun Iterable<Player>.findPlayerById(uuid: UUID) = find { it.uniqueId == uuid }

fun BlockVector.toVector() =
        Vector(this.x, this.y, this.z)

fun Vector.locationInWorld(world: World) = Location(world, x, y, z)

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

infix fun BlockVector.min(other: BlockVector) = BlockVector(min(x, other.x), min(y, other.y), min(z, other.z))

infix fun BlockVector.max(other: BlockVector) = BlockVector(max(x, other.x), max(y, other.y), max(z, other.z))

inline fun <T> Iterable<T>.runForEach(action: T.() -> Unit) { forEach { it.action() } }

fun Iterable<Int>.firstMissing() = find { !contains(it+1) }?.plus(1) ?: 0

inline fun textComponent(build: TextComponent.() -> Unit) = TextComponent().apply { build() }

inline fun textComponent(text: String, build: TextComponent.() -> Unit) = TextComponent(text).apply { build() }

inline fun TextComponent.extra(text: String, build: TextComponent.() -> Unit) = addExtra(textComponent(text, build))
inline fun TextComponent.extra(build: TextComponent.() -> Unit) = addExtra(textComponent(build))