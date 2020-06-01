package it.forgottenworld.dungeons.utils

import it.forgottenworld.dungeons.controller.FWDungeonsController
import it.forgottenworld.dungeons.model.party.Party
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.Player

fun Player.getTargetBlock() : Block? {
    return this.getTargetBlock(null as Set<Material>?, 5)
}

fun Player.getParty() : Party? =
    FWDungeonsController.parties.values.find {
        it.players.contains(this)
    }