package it.forgottenworld.dungeons.game.dungeon

import it.forgottenworld.dungeons.game.box.Box
import it.forgottenworld.dungeons.game.chest.Chest
import it.forgottenworld.dungeons.game.interactiveregion.ActiveArea
import it.forgottenworld.dungeons.game.interactiveregion.Trigger
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
    val chests: Map<Int, Chest>

    enum class Difficulty {

        EASY, MEDIUM, HARD;

        override fun toString() = when (this) {
            EASY -> "easy"
            MEDIUM -> "medium"
            HARD -> "hard"
        }

        companion object {
            fun fromString(value: String) = when (value) {
                "easy" -> EASY
                "medium" -> MEDIUM
                "hard" -> HARD
                else -> null
            }
        }
    }
}