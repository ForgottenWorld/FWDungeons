package it.forgottenworld.dungeons.command.play.dungeon

import it.forgottenworld.dungeons.cli.formatInvitation
import it.forgottenworld.dungeons.manager.DungeonManager.dungeonInstance
import it.forgottenworld.dungeons.utils.ktx.sendFWDMessage
import org.bukkit.Bukkit
import org.bukkit.entity.Player

fun cmdDungeonInvite(sender: Player, args: Array<out String>): Boolean {
    if (args.isEmpty()) {
        sender.sendFWDMessage("Please provide the name of whomever you want invite")
        return true
    }

    val instance = sender.dungeonInstance ?: run {
        sender.sendFWDMessage("You're currently not in a dungeon party")
        return true
    }

    if (instance.leader != sender) {
        sender.sendFWDMessage("Only the dungeon party leader may invite others to join")
        return true
    }

    val toPlayer = Bukkit.getServer().getPlayer(args[0]) ?: run {
        sender.sendFWDMessage("No currently online player has this name")
        return true
    }

    toPlayer.spigot().sendMessage(*formatInvitation(
            sender.name,
            instance.dungeon.id,
            instance.id,
            instance.partyKey
    ))

    sender.sendFWDMessage("Invite sent!")

    return true
}