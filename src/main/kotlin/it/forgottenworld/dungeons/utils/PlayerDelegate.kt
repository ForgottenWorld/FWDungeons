package it.forgottenworld.dungeons.utils

import org.bukkit.entity.Player
import kotlin.reflect.KProperty

class PlayerDelegate private constructor(player: Player? = null) {

    var uuid = player?.uniqueId

    operator fun getValue(thisRef: Any?, property: KProperty<*>) = uuid?.let { getPlayer(it) }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Player?) {
        uuid = value?.uniqueId
    }

    companion object {
        fun player(player: Player? = null) = PlayerDelegate(player)
    }
}