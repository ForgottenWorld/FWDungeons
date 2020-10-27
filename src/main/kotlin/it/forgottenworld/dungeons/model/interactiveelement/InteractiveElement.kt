package it.forgottenworld.dungeons.model.interactiveelement

import it.forgottenworld.dungeons.model.box.Box

enum class InteractiveElementType { TRIGGER, ACTIVE_AREA }

interface InteractiveElement {
    val id: Int
    val box: Box
}