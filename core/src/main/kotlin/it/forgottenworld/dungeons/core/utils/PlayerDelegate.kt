package it.forgottenworld.dungeons.core.utils

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

fun player(player: Player? = null) = object : ReadWriteProperty<Any?, Player?> {
    var uuid = player?.uniqueId

    override operator fun getValue(
        thisRef: Any?,
        property: KProperty<*>
    ) = uuid?.let { Bukkit.getPlayer(it) }

    override operator fun setValue(
        thisRef: Any?,
        property: KProperty<*>,
        value: Player?
    ) {
        uuid = value?.uniqueId
    }
}