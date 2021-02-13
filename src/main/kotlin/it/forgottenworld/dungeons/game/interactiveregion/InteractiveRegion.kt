package it.forgottenworld.dungeons.game.interactiveregion

import it.forgottenworld.dungeons.game.box.Box
import org.bukkit.util.BlockVector

interface InteractiveRegion {
    val id: Int
    val box: Box

    fun withContainerOrigin(oldOrigin: BlockVector, newOrigin: BlockVector): InteractiveRegion

    enum class Type { TRIGGER, ACTIVE_AREA }
}