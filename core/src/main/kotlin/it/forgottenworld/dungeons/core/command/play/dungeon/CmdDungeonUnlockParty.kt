package it.forgottenworld.dungeons.core.command.play.dungeon

import com.google.inject.Inject
import it.forgottenworld.dungeons.api.command.PlayerCommand
import it.forgottenworld.dungeons.core.cli.JsonMessageGenerator
import it.forgottenworld.dungeons.core.config.Strings
import it.forgottenworld.dungeons.core.game.dungeon.DungeonManager
import it.forgottenworld.dungeons.core.utils.sendPrefixedMessage
import it.forgottenworld.dungeons.core.utils.sendJsonMessage
import org.bukkit.entity.Player

class CmdDungeonUnlockParty @Inject constructor(
    private val jsonMessageGenerator: JsonMessageGenerator,
    private val dungeonManager: DungeonManager
) : PlayerCommand() {

    override fun command(sender: Player, args: Array<out String>): Boolean {

        val instance = dungeonManager.getPlayerInstance(sender.uniqueId) ?: run {
            sender.sendPrefixedMessage(Strings.CURRENTLY_NOT_IN_DUNGEON_PARTY)
            return true
        }

        when {
            !instance.isLocked -> sender.sendPrefixedMessage(Strings.DUNGEON_PARTY_ALREADY_PUBLIC)
            sender.uniqueId == instance.leader -> {
                instance.unlock()
                sender.sendJsonMessage {
                    append("${Strings.CHAT_PREFIX}The dungeon party is now public, anyone can join. To make it private, click ")
                    append(jsonMessageGenerator.lockLink())
                }
            }
            else -> sender.sendPrefixedMessage(Strings.ONLY_LEADER_MAY_OPEN_PARTY)
        }

        return true
    }
}