package it.forgottenworld.dungeons.command.play.dungeon

import it.forgottenworld.dungeons.model.instance.DungeonFinalInstance.Companion.finalInstance
import it.forgottenworld.dungeons.utils.ktx.sendFWDMessage
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender

fun cmdDungeonPlayerLookup(sender: CommandSender, args: Array<out String>): Boolean {
    if (args.count() == 0) {
        sender.sendFWDMessage("Please provide a player name")
        return true
    }

    val player = Bukkit.getPlayer(args[0])
            ?: run {
                sender.sendFWDMessage("Player not found")
                return true
            }

    val instance = player.finalInstance
            ?: run {
                sender.sendFWDMessage("Player is not in a party or an instance")
                return true
            }

    sender.sendFWDMessage(
            "Player ${args[0]} is in a party for dungeon " +
            "(id: ${instance.dungeon.id}), " +
            "instance (id: ${instance.id})"
    )

    return true
}