package it.forgottenworld.dungeons.utils

import it.forgottenworld.dungeons.controller.FWDungeonsController
import it.forgottenworld.dungeons.model.activearea.ActiveArea
import it.forgottenworld.dungeons.model.dungeon.DungeonInstance
import it.forgottenworld.dungeons.model.party.Party
import it.forgottenworld.dungeons.model.trigger.Trigger
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.util.BlockVector
import org.bukkit.util.Vector
import java.util.*

fun Iterable<Trigger>.toTriggerIdMap() =
        map{ it.id }.zip(this).toMap()

fun Iterable<ActiveArea>.toActiveAreaIdMap() =
        map{ it.id }.zip(this).toMap()

fun Player.getTargetBlock() : Block? =
        this.getTargetBlock(null as Set<Material>?, 5)

fun Player.getParty() : Party? =
        FWDungeonsController.playerParties[uniqueId]

fun Player.getDungeonInstance() : DungeonInstance? =
        FWDungeonsController.playerParties[uniqueId]?.instance

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

fun BlockVector.withRefSystemOrigin(oldOrigin: BlockVector, newOrigin: BlockVector) =
        BlockVector(
                this.x - oldOrigin.x + newOrigin.x,
                this.y - oldOrigin.y + newOrigin.y,
                this.z - oldOrigin.z + newOrigin.z)