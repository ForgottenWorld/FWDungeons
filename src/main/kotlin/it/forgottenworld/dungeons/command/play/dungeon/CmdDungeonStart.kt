package it.forgottenworld.dungeons.command.play.dungeon

import it.forgottenworld.dungeons.config.ConfigManager
import it.forgottenworld.dungeons.state.DungeonState.party
import it.forgottenworld.dungeons.state.DungeonState.returnGameMode
import it.forgottenworld.dungeons.state.DungeonState.returnPosition
import it.forgottenworld.dungeons.utils.sendFWDMessage
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Location
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
            party.inGame = true
            party.players.forEach { p ->
                p.returnPosition = p.location
                p.returnGameMode = p.gameMode
                p.gameMode = GameMode.ADVENTURE
                p.teleport(
                        Location(
                                Bukkit.getWorld(ConfigManager.dungeonWorld),
                                party.instance.startingPostion.x,
                                party.instance.startingPostion.y,
                                party.instance.startingPostion.z))
                p.sendFWDMessage("Good luck out there!")
            }
            sender.sendFWDMessage("Dungeon party members have been teleported to the dungeon entrance")
        }
    }

    return true
}