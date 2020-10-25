package it.forgottenworld.dungeons.model

data class MobSpawnData(
        val activeAreaId: Int,
        val mob: String,
        val isMythic: Boolean)