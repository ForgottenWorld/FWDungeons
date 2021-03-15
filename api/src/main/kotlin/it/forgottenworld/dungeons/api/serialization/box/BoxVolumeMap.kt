package it.forgottenworld.dungeons.api.serialization.box

import it.forgottenworld.dungeons.api.math.Box
import org.bukkit.World
import java.io.File
import java.io.IOException

class BoxVolumeMap private constructor(
    world: World,
    box: Box
) {

    enum class BlockValue(val raw: Byte) {
        AIR(0x00),
        SOLID(0x01),
        LIQUID(0x02),
        PASSABLE(0x03)
    }

    private var raw = serializeSize(box) + serializeVolume(world, box)

    private fun serializeVolume(world: World, box: Box): ByteArray {
        val res = mutableListOf<Byte>()
        for (y in 0 until box.height) {
            for (z in 0 until box.depth) {
                for (x in 0 until box.width) {
                    res.add(
                        world.getBlockAt(
                            box.origin.x + x,
                            box.origin.y + y,
                            box.origin.z + z
                        ).let {
                            when {
                                it.isSolid -> BlockValue.SOLID
                                it.isLiquid -> BlockValue.LIQUID
                                it.isEmpty -> BlockValue.AIR
                                it.isPassable -> BlockValue.PASSABLE
                                else -> BlockValue.AIR
                            }.raw
                        }
                    )
                }
            }
        }
        return res.toByteArray()
    }

    private fun serializeSize(box: Box) =
        box.width.toByteArray() +
        box.height.toByteArray() +
        box.depth.toByteArray()

    fun saveToFile(dir: File, name: String) = try {
        File(dir, "$name.fwdbx").writeBytes(raw)
        true
    } catch (e: IOException) {
        false
    }

    companion object {
        fun Box.getVolumeMap(world: World) = BoxVolumeMap(world, this)

        private fun Int.toByteArray() = byteArrayOf(
            (this shr 24).toByte(),
            (this shr 16).toByte(),
            (this shr 8).toByte(),
            this.toByte()
        )
    }
}