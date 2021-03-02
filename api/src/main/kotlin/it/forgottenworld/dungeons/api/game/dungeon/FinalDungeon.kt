package it.forgottenworld.dungeons.api.game.dungeon

import it.forgottenworld.dungeons.api.math.Box
import it.forgottenworld.dungeons.api.math.NestableGrid3iToNi
import it.forgottenworld.dungeons.api.math.Vector3i
import org.bukkit.entity.Player

interface FinalDungeon : Dungeon {

    override val startingLocation: Vector3i

    override val box: Box

    var isActive: Boolean

    var isBeingEdited: Boolean

    val triggerGrid: NestableGrid3iToNi

    fun putInEditMode(player: Player): EditableDungeon?

    fun import(at: Vector3i): Boolean
}