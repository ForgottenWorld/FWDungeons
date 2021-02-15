package it.forgottenworld.dungeons.utils

import org.bukkit.entity.Player
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

fun player(player: Player? = null) = object : ReadWriteProperty<Any?, Player?> {
    var uuid = player?.uniqueId

    override operator fun getValue(
        thisRef: Any?,
        property: KProperty<*>
    ) = uuid?.let { getPlayer(it) }

    override operator fun setValue(
        thisRef: Any?,
        property: KProperty<*>,
        value: Player?
    ) {
        uuid = value?.uniqueId
    }
}