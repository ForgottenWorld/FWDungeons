package it.forgottenworld.dungeons.game.interactiveregion

import it.forgottenworld.dungeons.game.box.Box
import it.forgottenworld.dungeons.game.instance.DungeonInstance
import it.forgottenworld.dungeons.utils.Vector3i

interface InteractiveRegion {
    val id: Int
    val box: Box

    fun withContainerOrigin(oldOrigin: Vector3i, newOrigin: Vector3i): InteractiveRegion

    fun boxInInstance(dungeonInstance: DungeonInstance) = box
        .withContainerOrigin(Vector3i.ZERO, dungeonInstance.origin)

    enum class Type { TRIGGER, ACTIVE_AREA }
}