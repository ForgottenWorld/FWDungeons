package it.forgottenworld.dungeons.command.play.dungeon

import it.forgottenworld.dungeons.config.Strings
import it.forgottenworld.dungeons.model.instance.DungeonFinalInstance.Companion.finalInstance
import it.forgottenworld.dungeons.utils.sendFWDMessage
import org.bukkit.entity.Player

fun cmdDungeonStart(sender: Player, args: Array<out String>): Boolean {
    val instance = sender.finalInstance ?: run {
        sender.sendFWDMessage(Strings.CURRENTLY_NOT_IN_DUNGEON_PARTY)
        return true
    }

    when {
        instance.leader != sender ->
            sender.sendFWDMessage(Strings.ONLY_LEADER_MAY_START_INSTANCE)
        instance.players.size < instance.minPlayers ->
            sender.sendFWDMessage(Strings.NOT_ENOUGH_PLAYERS_FOR_DUNGEON)
        else -> {
            sender.finalInstance?.onStart()
            sender.sendFWDMessage(Strings.DUNGEON_PARTY_MEMBERS_HAVE_BEEN_TPED)
        }
    }

    return true
}