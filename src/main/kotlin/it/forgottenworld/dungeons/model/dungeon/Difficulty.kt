package it.forgottenworld.dungeons.model.dungeon

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