package it.forgottenworld.dungeons.model.dungeon

enum class Difficulty {
    EASY, MEDIUM, HARD;

    companion object {
        fun fromString(value: String) = when (value) {
            "easy" -> EASY
            "medium" -> MEDIUM
            "hard" -> HARD
            else -> null
        }
    }

    override fun toString() = when (this) {
        EASY -> "easy"
        MEDIUM -> "medium"
        HARD -> "hard"
    }
}