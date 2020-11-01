package it.forgottenworld.dungeons.model.box

import org.bukkit.util.BlockVector

class BoxBuilder {

    private var pos1: BlockVector? = null
    private var pos2: BlockVector? = null

    private val canBeBuilt
        get() = pos1 != null && pos2 != null

    fun clear() {
        pos1 = null
        pos2 = null
    }

    fun pos1(pos: BlockVector) {
        pos1 = pos
    }

    fun pos2(pos: BlockVector) {
        pos2 = pos
    }

    fun build(): Box? {
        if (!canBeBuilt) return null
        val box = Box(pos1!!, pos2!!)
        clear()
        return box
    }
}