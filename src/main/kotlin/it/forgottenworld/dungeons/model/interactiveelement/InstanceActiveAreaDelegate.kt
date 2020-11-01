package it.forgottenworld.dungeons.model.interactiveelement

import it.forgottenworld.dungeons.model.dungeon.FinalDungeon
import it.forgottenworld.dungeons.model.instance.DungeonFinalInstance
import org.bukkit.util.BlockVector
import kotlin.reflect.KProperty

fun DungeonFinalInstance.instanceActiveAreas() = FinalInstanceActiveAreaDelegate(dungeon, origin)

class FinalInstanceActiveAreaDelegate(dungeon: FinalDungeon, newOrigin: BlockVector) {

    private val activeAreas = dungeon
            .activeAreas
            .map { (k, v) -> k to v.withContainerOrigin(BlockVector(0, 0, 0), newOrigin) }
            .toMap()

    operator fun getValue(thisRef: Any?, property: KProperty<*>) = activeAreas
}