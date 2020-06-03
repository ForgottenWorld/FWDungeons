package it.forgottenworld.dungeons.utils

import it.forgottenworld.dungeons.controller.FWDungeonsController
import it.forgottenworld.dungeons.model.party.Party
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.util.BlockVector
import org.bukkit.util.Vector
import java.util.*

fun Player.getTargetBlock() : Block? =
        this.getTargetBlock(null as Set<Material>?, 5)

fun Player.getParty() : Party? =
        FWDungeonsController.parties.values.find {
            it.players.contains(this)
        }

fun Block.getBlockVector() : BlockVector =
        BlockVector(this.x, this.y, this.z)

fun Player.idEquals(player: Player) =
        this.uniqueId == player.uniqueId

fun Iterable<Player>.findPlayerById(player: Player) : Player? =
        this.find { it.idEquals(player) }

fun Iterable<Player>.findPlayerById(uuid: UUID) : Player? =
        this.find { it.uniqueId == uuid }

fun BlockVector.toVector() : Vector =
        Vector(this.x, this.y, this.z)