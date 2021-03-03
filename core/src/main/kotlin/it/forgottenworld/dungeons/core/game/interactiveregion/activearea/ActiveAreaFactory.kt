package it.forgottenworld.dungeons.core.game.interactiveregion.activearea

import it.forgottenworld.dungeons.api.game.interactiveregion.ActiveArea
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