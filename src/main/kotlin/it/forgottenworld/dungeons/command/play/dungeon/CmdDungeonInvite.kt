package it.forgottenworld.dungeons.command.play.dungeon

import it.forgottenworld.dungeons.cli.formatInvitation
import it.forgottenworld.dungeons.config.Strings
import it.forgottenworld.dungeons.model.instance.DungeonFinalInstance.Companion.finalInstance
import it.forgottenworld.dungeons.utils.sendFWDMessage
import org.bukkit.Bukkit
import org.bukkit.entity.Player

fun cmdDungeonInvite(sender: Player, args: Array<out String>): Boolean {
    if (args.isEmpty()) {
        sender.sendFWDMessage(Strings.PROVIDE_NAME_OF_INVITEE)
        return true
    }

    val instance = sender.finalInstance ?: run {
        sender.sendFWDMessage(Strings.CURRENTLY_NOT_IN_DUNGEON_PARTY)
        return true
    }

    if (instance.leader != sender) {
        sender.sendFWDMessage(Strings.ONLY_LEADER_MAY_INVITE)
        return true
    }

    val toPlayer = Bukkit.getServer().getPlayer(args[0]) ?: run {
        sender.sendFWDMessage(Strings.NO_ONLINE_PLAYER_HAS_THIS_NAME)
        return true
    }

    toPlayer.spigot().sendMessage(*formatInvitation(
        sender.name,
        instance.dungeon.id,
        instance.id,
        instance.partyKey
    ))

    sender.sendFWDMessage(Strings.INVITE_SENT)

    return true
}