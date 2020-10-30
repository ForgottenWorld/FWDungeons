package it.forgottenworld.dungeons.model.interactiveelement

import it.forgottenworld.dungeons.model.dungeon.Dungeon
import it.forgottenworld.dungeons.model.instance.DungeonFinalInstance
import org.bukkit.util.BlockVector
import kotlin.reflect.KProperty

fun DungeonFinalInstance.instanceTriggers() = FinalInstanceTriggerDelegate(dungeon, origin)


class FinalInstanceTriggerDelegate(dungeon: Dungeon, newOrigin: BlockVector) {

    private val triggers = dungeon
            .triggers
            .map { (k, v) -> k to v.withContainerOrigin(BlockVector(0, 0, 0), newOrigin) }
            .toMap()

    operator fun getValue(thisRef: Any?, property: KProperty<*>) = triggers
}