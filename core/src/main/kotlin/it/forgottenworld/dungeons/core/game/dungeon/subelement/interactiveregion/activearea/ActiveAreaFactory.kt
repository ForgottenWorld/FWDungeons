package it.forgottenworld.dungeons.core.game.dungeon.subelement.interactiveregion.activearea

import it.forgottenworld.dungeons.api.game.dungeon.subelement.interactiveregion.ActiveArea
import it.forgottenworld.dungeons.api.math.Box
import org.bukkit.Material

interface ActiveAreaFactory {
    fun create(
        id: Int,
        box: Box,
        startingMaterial: Material,
        label: String? = null,
    ) : ActiveArea
}