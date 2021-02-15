package it.forgottenworld.dungeons.utils

import io.lumine.xikage.mythicmobs.api.bukkit.BukkitAPIHelper
import it.forgottenworld.dungeons.FWDungeonsPlugin
import it.forgottenworld.dungeons.config.ConfigManager
import it.forgottenworld.dungeons.config.Strings
import it.forgottenworld.dungeons.game.box.Box
import it.forgottenworld.dungeons.game.detection.CubeGridUtils
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

val Block.vector3i
    get() = Vector3i(x, y, z)

fun Location.toVector3i() = Vector3i(blockX, blockY, blockZ)

fun Vector3i.locationInWorld(world: World) = Location(world, x.toDouble(), y.toDouble(), z.toDouble())

fun Vector3i.toVector() = Vector(x, y, z)

fun Vector.toVector3i() = Vector3i(blockX, blockY, blockZ)

fun Vector3i.toBlockVector() = BlockVector(x, y, z)

infix fun Vector3i.min(other: Vector3i) = Vector3i(
    min(x, other.x),
    min(y, other.y),
    min(z, other.z)
)

infix fun Vector3i.max(other: Vector3i) = Vector3i(
    max(x, other.x),
    max(y, other.y),
    max(z, other.z)
)

fun World.getBlockAt(vector3i: Vector3i) = getBlockAt(
    vector3i.x,
    vector3i.y,
    vector3i.z
)

fun Vector3i.withRefSystemOrigin(
    oldOrigin: Vector3i,
    newOrigin: Vector3i
) = Vector3i(
    x - oldOrigin.x + newOrigin.x,
    y - oldOrigin.y + newOrigin.y,
    z - oldOrigin.z + newOrigin.z
)

val Vector3i.box
    get() = Box(
        this,
        CubeGridUtils.GRID_INITIAL_CELL_SIZE,
        CubeGridUtils.GRID_INITIAL_CELL_SIZE,
        CubeGridUtils.GRID_INITIAL_CELL_SIZE
    )

fun Int.toByteArray() = byteArrayOf(
    (this shr 24).toByte(),
    (this shr 16).toByte(),
    (this shr 8).toByte(),
    this.toByte()
)

fun CommandSender.sendFWDMessage(message: String) = sendMessage("${Strings.CHAT_PREFIX}$message")

fun Iterable<Int>.firstMissing() = find { !contains(it + 1) }?.plus(1) ?: 0

fun getPlayer(uuid: UUID) = Bukkit.getPlayer(uuid)

val plugin get() = JavaPlugin.getPlugin(FWDungeonsPlugin::class.java)

val mythicMobsHelper by lazy { BukkitAPIHelper() }

val dungeonWorld get() = ConfigManager.dungeonWorld

infix fun Int.euclideanMod(other: Int) = (this % other + other) % other