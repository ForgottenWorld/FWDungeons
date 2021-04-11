package it.forgottenworld.dungeons.core.game.dungeon.subelement.interactiveregion.spawnarea

import it.forgottenworld.dungeons.api.game.dungeon.subelement.interactiveregion.SpawnArea
import it.forgottenworld.dungeons.api.math.Box

interface SpawnAreaFactory {
    fun create(
        id: Int,
        box: Box,
        heightMap: Array<IntArray>,
        label: String? = null,
    ) : SpawnArea
}