package it.forgottenworld.dungeons.utils.ktx

import org.bukkit.entity.Player
import java.util.*
import kotlin.reflect.KProperty

fun safePlayer() = SafePlayer()

class SafePlayer {

    var uuid: UUID? = null

    operator fun getValue(thisRef: Any?, property: KProperty<*>) = uuid?.let { getPlayer(it) }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Player?) {
        uuid = value?.uniqueId
    }
}