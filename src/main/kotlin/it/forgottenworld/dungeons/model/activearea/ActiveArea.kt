package it.forgottenworld.dungeons.model.activearea

import it.forgottenworld.dungeons.config.ConfigManager
import it.forgottenworld.dungeons.model.box.Box
import org.bukkit.Bukkit.getWorld
import org.bukkit.Material

class ActiveArea(val id:Int, val box:Box, val startingMaterial: Material = Material.AIR) {

    fun fillWithMaterial(material: Material) {
        box.getAllBlocks().forEach {
            it.setType(material, true)
        }
    }

}