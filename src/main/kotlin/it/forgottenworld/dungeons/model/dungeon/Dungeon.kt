package it.forgottenworld.dungeons.model.dungeon

import it.forgottenworld.dungeons.model.box.Box
import it.forgottenworld.dungeons.model.interactiveelement.ActiveArea
import it.forgottenworld.dungeons.model.interactiveelement.Trigger
import org.bukkit.util.BlockVector

interface Dungeon {
    val id: Int
    val name: String
    val description: String
    val difficulty: Difficulty
    val points: Int
    val numberOfPlayers: IntRange
    val box: Box?
    val startingLocation: BlockVector?
    val triggers: Map<Int, Trigger>
    val activeAreas: Map<Int, ActiveArea>
}