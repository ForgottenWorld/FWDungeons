package it.forgottenworld.dungeons.api.game.unlockables

interface UnlockableSeries {
    val id: Int
    val name: String
    val description: String
    val unlockables: List<Unlockable>
}