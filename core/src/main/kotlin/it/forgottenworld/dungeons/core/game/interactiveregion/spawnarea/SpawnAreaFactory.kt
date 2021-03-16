package it.forgottenworld.dungeons.core.game.interactiveregion.spawnarea

import it.forgottenworld.dungeons.api.game.interactiveregion.SpawnArea
import it.forgottenworld.dungeons.api.math.Box

interface SpawnAreaFactory {
    fun create(
        id: Int,
        box: Box,
        heightMap: Array<IntArray>,
        label: String? = null,
    ) : SpawnArea
}