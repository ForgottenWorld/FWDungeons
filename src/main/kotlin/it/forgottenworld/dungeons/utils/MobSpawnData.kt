package it.forgottenworld.dungeons.utils

data class MobSpawnData(
        val activeAreaId: Int,
        val mob: String,
        val isMythic: Boolean) : Cloneable {

    public override fun clone() = MobSpawnData(activeAreaId, mob, isMythic)
}