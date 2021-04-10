package it.forgottenworld.dungeons.api.game.dungeon

import it.forgottenworld.dungeons.api.game.dungeon.subelement.chest.Chest
import it.forgottenworld.dungeons.api.game.dungeon.subelement.interactiveregion.ActiveArea
import it.forgottenworld.dungeons.api.game.dungeon.subelement.interactiveregion.SpawnArea
import it.forgottenworld.dungeons.api.game.dungeon.subelement.interactiveregion.Trigger
import it.forgottenworld.dungeons.api.math.Box
import it.forgottenworld.dungeons.api.math.Vector3i
import it.forgottenworld.dungeons.api.storage.Storage

interface Dungeon : Storage.Storable {

    val id: Int

    val name: String

    val description: String

    val difficulty: Difficulty

    val points: Int

    val minPlayers: Int

    val maxPlayers: Int

    val box: Box?

    val startingLocation: Vector3i?

    val triggers: Map<Int, Trigger>

    val activeAreas: Map<Int, ActiveArea>

    val spawnAreas: Map<Int, SpawnArea>

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