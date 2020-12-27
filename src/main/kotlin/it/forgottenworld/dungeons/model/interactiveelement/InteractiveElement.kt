package it.forgottenworld.dungeons.model.interactiveelement

import it.forgottenworld.dungeons.model.box.Box
import org.bukkit.util.BlockVector

enum class InteractiveElementType { TRIGGER, ACTIVE_AREA }

interface InteractiveElement {
    val id: Int
    val box: Box

    fun withContainerOrigin(oldOrigin: BlockVector, newOrigin: BlockVector): InteractiveElement
}