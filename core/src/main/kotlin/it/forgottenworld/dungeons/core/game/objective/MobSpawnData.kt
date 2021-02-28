package it.forgottenworld.dungeons.core.game.objective

data class MobSpawnData(
    val activeAreaId: Int,
    val mob: String,
    val isMythic: Boolean
)