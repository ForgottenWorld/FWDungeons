package it.forgottenworld.dungeons.command.play.dungeon

import it.forgottenworld.dungeons.manager.DungeonManager.dungeonInstance
import it.forgottenworld.dungeons.utils.ktx.sendFWDMessage
import org.bukkit.entity.Player

fun cmdDungeonStart(sender: Player, args: Array<out String>): Boolean {
    val instance = sender.dungeonInstance ?: run {
        sender.sendFWDMessage("You're currently not in a dungeon party")
        return true
    }

    when {
        instance.leader != sender ->
            sender.sendFWDMessage("Only the dungeon party leader may start the instance")
        instance.players.size < instance.minPlayers ->
            sender.sendFWDMessage("Not enough players for this dungeon")
        else -> {
            sender.dungeonInstance?.onStart()
            sender.sendFWDMessage("Dungeon party members have been teleported to the dungeon entrance")
        }
    }

    return true
}