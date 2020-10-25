package it.forgottenworld.dungeons.command.play.dungeon

import it.forgottenworld.dungeons.manager.DungeonManager.dungeonInstance
import it.forgottenworld.dungeons.manager.DungeonManager.party
import it.forgottenworld.dungeons.utils.sendFWDMessage
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

fun cmdDungeonStart(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
    if (sender !is Player) return true

    val party = sender.party ?: run {
        sender.sendFWDMessage("You're currently not in a dungeon party")
        return true
    }

    when {
        party.leader != sender ->
            sender.sendFWDMessage("Only the dungeon party leader may start the instance")
        party.players.count() < party.instance.dungeon.numberOfPlayers.first ->
            sender.sendFWDMessage("Not enough players for this dungeon")
        else -> {
            sender.dungeonInstance?.onInstanceStart()
            sender.sendFWDMessage("Dungeon party members have been teleported to the dungeon entrance")
        }
    }

    return true
}