package it.forgottenworld.dungeons.model.dungeon

import kotlin.reflect.KProperty

class FinalDungeonsDelegate private constructor(val id: Int) {

    operator fun getValue(thisRef: Any?, property: KProperty<*>) =
        FinalDungeon.dungeons[id] ?: error("Dungeon $id was not found")

    companion object {
        fun finalDungeons(id: Int) = FinalDungeonsDelegate(id)
    }
}