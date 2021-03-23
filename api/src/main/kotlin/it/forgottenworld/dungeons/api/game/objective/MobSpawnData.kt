package it.forgottenworld.dungeons.api.game.objective

data class MobSpawnData(
    val spawnAreaId: Int,
    val mob: String,
    val isMythic: Boolean
)