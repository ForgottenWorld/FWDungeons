package it.forgottenworld.dungeons.core.command.play.dungeon

import it.forgottenworld.dungeons.api.command.PlayerCommand
import it.forgottenworld.dungeons.core.cli.JsonMessages
import it.forgottenworld.dungeons.core.config.Strings
import it.forgottenworld.dungeons.core.game.dungeon.DungeonManager.finalInstance
import it.forgottenworld.dungeons.core.utils.sendFWDMessage
import it.forgottenworld.dungeons.core.utils.sendJsonMessage
import org.bukkit.entity.Player

class CmdDungeonUnlockParty : PlayerCommand() {

    override fun command(sender: Player, args: Array<out String>): Boolean {

        val instance = sender.uniqueId.finalInstance ?: run {
            sender.sendFWDMessage(Strings.CURRENTLY_NOT_IN_DUNGEON_PARTY)
            return true
        }

        when {
            !instance.isLocked -> sender.sendFWDMessage(Strings.DUNGEON_PARTY_ALREADY_PUBLIC)
            sender.uniqueId == instance.leader -> {
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