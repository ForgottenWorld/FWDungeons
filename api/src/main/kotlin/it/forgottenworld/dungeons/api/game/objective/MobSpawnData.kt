package it.forgottenworld.dungeons.api.game.objective

data class MobSpawnData(
    val activeAreaId: Int,
    val mob: String,
    val isMythic: Boolean
)