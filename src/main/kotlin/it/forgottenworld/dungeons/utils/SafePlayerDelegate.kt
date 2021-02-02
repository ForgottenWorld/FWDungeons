package it.forgottenworld.dungeons.utils

import org.bukkit.entity.Player
import kotlin.reflect.KProperty

fun safePlayer(player: Player? = null) = SafePlayerDelegate(player)

class SafePlayerDelegate(player: Player? = null) {

    var uuid = player?.uniqueId

    operator fun getValue(thisRef: Any?, property: KProperty<*>) = uuid?.let { getPlayer(it) }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Player?) {
        uuid = value?.uniqueId
    }
}