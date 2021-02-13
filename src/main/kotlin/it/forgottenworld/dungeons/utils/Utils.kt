package it.forgottenworld.dungeons.utils

import io.lumine.xikage.mythicmobs.api.bukkit.BukkitAPIHelper
import it.forgottenworld.dungeons.FWDungeonsPlugin
import it.forgottenworld.dungeons.config.Strings
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.block.Block
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.util.BlockVector
import org.bukkit.util.Vector
import java.util.*
import kotlin.math.max
import kotlin.math.min

val Player.targetBlock
    get() = getTargetBlock(null as Set<Material>?, 5)

val Block.blockVector
    get() = BlockVector(x, y, z)

fun BlockVector.toVector() = Vector(x, y, z)

fun Location.toBlockVector() = BlockVector(blockX, blockY, blockZ)

fun Vector.locationInWorld(world: World) = Location(world, x, y, z)

infix fun Int.euclideanMod(other: Int) = (this % other + other) % other

fun IntRange.intersects(other: IntRange) = last >= other.first && first <= other.last

fun BlockVector.withRefSystemOrigin(
    oldOrigin: BlockVector,
    newOrigin: BlockVector
) = BlockVector(
    x - oldOrigin.x + newOrigin.x,
    y - oldOrigin.y + newOrigin.y,
    z - oldOrigin.z + newOrigin.z
)

fun Int.toByteArray() = byteArrayOf(
    (this shr 24).toByte(),
    (this shr 16).toByte(),
    (this shr 8).toByte(),
    this.toByte()
)

fun CommandSender.sendFWDMessage(message: String) = sendMessage("${Strings.CHAT_PREFIX}$message")

infix fun BlockVector.min(other: BlockVector) = BlockVector(min(x, other.x), min(y, other.y), min(z, other.z))

infix fun BlockVector.max(other: BlockVector) = BlockVector(max(x, other.x), max(y, other.y), max(z, other.z))

fun Iterable<Int>.firstMissing() = find { !contains(it + 1) }?.plus(1) ?: 0

fun getPlayer(uuid: UUID) = Bukkit.getPlayer(uuid)

val plugin get() = JavaPlugin.getPlugin(FWDungeonsPlugin::class.java)

val mythicMobsHelper by lazy { BukkitAPIHelper() }