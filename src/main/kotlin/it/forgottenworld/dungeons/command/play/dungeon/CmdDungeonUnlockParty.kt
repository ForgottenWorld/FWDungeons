package it.forgottenworld.dungeons.command.play.dungeon

import it.forgottenworld.dungeons.cli.JsonMessages
import it.forgottenworld.dungeons.command.api.CommandHandler
import it.forgottenworld.dungeons.config.Strings
import it.forgottenworld.dungeons.game.instance.DungeonFinalInstance.Companion.finalInstance
import it.forgottenworld.dungeons.utils.sendFWDMessage
import it.forgottenworld.dungeons.utils.sendJsonMessage
import org.bukkit.entity.Player

class CmdDungeonUnlockParty : CommandHandler<Player> {

    override fun command(sender: Player, args: Array<out String>): Boolean {

        val instance = sender.finalInstance ?: run {
            sender.sendFWDMessage(Strings.CURRENTLY_NOT_IN_DUNGEON_PARTY)
            return true
        }

        when {
            !instance.isLocked -> sender.sendFWDMessage(Strings.DUNGEON_PARTY_ALREADY_PUBLIC)
            sender == instance.leader -> {
                instance.unlock()
                sender.sendJsonMessage {
                    append("${Strings.CHAT_PREFIX}The dungeon party is now public, anyone can join. To make it private, click ")
                    append(JsonMessages.lockLink())
                }
            }
            else -> sender.sendFWDMessage(Strings.ONLY_LEADER_MAY_OPEN_PARTY)
        }

        return true
    }
}