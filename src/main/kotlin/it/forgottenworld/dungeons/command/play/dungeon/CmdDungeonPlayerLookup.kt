package it.forgottenworld.dungeons.command.play.dungeon

import it.forgottenworld.dungeons.state.DungeonState.party
import it.forgottenworld.dungeons.utils.sendFWDMessage
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender

fun cmdDungeonPlayerLookup(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
    if (args.count() == 0) {
        sender.sendFWDMessage("Please provide a player name")
        return true
    }

    val player = Bukkit.getPlayer(args[0])
            ?: run {
                sender.sendFWDMessage("Player not found")
                return true
            }

    val party = player.party
            ?: run {
                sender.sendFWDMessage("Player is not in a party or an instance")
                return true
            }

    sender.sendFWDMessage(
            "Player ${args[0]} is in a party for dungeon " +
            "(id: ${party.instance.dungeon.id}), " +
            "instance (id: ${party.instance.id})"
    )

    return true
}