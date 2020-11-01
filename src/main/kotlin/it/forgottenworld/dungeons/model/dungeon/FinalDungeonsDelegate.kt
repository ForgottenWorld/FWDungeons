package it.forgottenworld.dungeons.model.dungeon

import kotlin.reflect.KProperty

fun finalDungeons(id: Int) = FinalDungeonsDelegate(id)

class FinalDungeonsDelegate(val id: Int) {

    operator fun getValue(thisRef: Any?, property: KProperty<*>) =
            FinalDungeon.dungeons[id] ?: error("Dungeon $id was not found")
}