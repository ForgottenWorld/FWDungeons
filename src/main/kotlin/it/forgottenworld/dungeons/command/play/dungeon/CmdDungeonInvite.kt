package it.forgottenworld.dungeons.command.play.dungeon

import it.forgottenworld.dungeons.cli.formatInvitation
import it.forgottenworld.dungeons.state.DungeonState.dungeonInstance
import it.forgottenworld.dungeons.utils.sendFWDMessage
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

fun cmdDungeonInvite(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
    if (sender !is Player) return true

    if (args.isEmpty()) {
        sender.sendFWDMessage("Please provide the name of whomever you want invite")
        return true
    }

    val instance = sender.dungeonInstance ?: run {
        sender.sendFWDMessage("You're currently not in a dungeon party")
        return true
    }

    val party = instance.party
    if (party?.leader != sender) {
        sender.sendFWDMessage("Only the dungeon party leader may invite others to join")
        return true
    }

    val toPlayer = Bukkit.getServer().getPlayer(args[0]) ?: run {
        sender.sendFWDMessage("No currently online player has this name")
        return true
    }

    toPlayer.spigot().sendMessage(formatInvitation(
            sender.name,
            instance.dungeon.id,
            instance.id,
            party.partyKey
    ))

    sender.sendFWDMessage("Invite sent!")

    return true
}