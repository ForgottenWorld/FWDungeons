package it.forgottenworld.dungeons.model.trigger

import it.forgottenworld.dungeons.config.ConfigManager
import it.forgottenworld.dungeons.controller.FWDungeonsController
import it.forgottenworld.dungeons.model.box.Box
import it.forgottenworld.dungeons.model.dungeon.Dungeon
import org.bukkit.entity.Player
import org.bukkit.util.BlockVector

class Trigger(
        val id: Int,
        val dungeon: Dungeon,
        val box: Box,
        val effect: (Player) -> Unit,
        val requiresWholeParty: Boolean = false) {

    val playersCurrentlyInside = mutableListOf<Player>()
    var active = true
    val origin : BlockVector
        get() = box.origin

    fun isPlayerTriggering(player: Player) = box.containsPlayer(player)

    fun onPlayerEnter(player: Player) {
        if (!playersCurrentlyInside.contains(player)) {
            FWDungeonsController
                    .playersTriggering[player.uniqueId] = this
            if (ConfigManager.isInDebugMode)
                player.sendMessage("Entered trigger zone id $id in dungeon id ${dungeon.id}")
            playersCurrentlyInside.add(player)
        }
    }

    fun onPlayerExit(player: Player) {
        if (ConfigManager.isInDebugMode)
            player.sendMessage("Exited trigger zone id $id in dungeon id ${dungeon.id}")
        playersCurrentlyInside.remove(player)
        FWDungeonsController
                .playersTriggering.remove(player.uniqueId)
    }
}