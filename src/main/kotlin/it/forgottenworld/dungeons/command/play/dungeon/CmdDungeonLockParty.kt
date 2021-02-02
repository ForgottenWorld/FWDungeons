package it.forgottenworld.dungeons.command.play.dungeon

import it.forgottenworld.dungeons.cli.getUnlockClickable
import it.forgottenworld.dungeons.config.Strings
import it.forgottenworld.dungeons.model.instance.DungeonFinalInstance.Companion.finalInstance
import it.forgottenworld.dungeons.utils.chatComponent
import it.forgottenworld.dungeons.utils.sendFWDMessage
import org.bukkit.entity.Player

fun cmdDungeonLockParty(sender: Player, args: Array<out String>): Boolean {
    val instance = sender.finalInstance ?: run {
        sender.sendFWDMessage(Strings.CURRENTLY_NOT_IN_DUNGEON_PARTY)
        return true
    }

    when {
        instance.isLocked -> sender.sendFWDMessage(Strings.DUNGEON_PARTY_ALREADY_PRIVATE)
        sender == instance.leader -> {
            instance.lock()
            sender.spigot().sendMessage(*chatComponent {
                append("${Strings.CHAT_PREFIX}${Strings.PARTY_NOW_PRIVATE_INVITE_WITH_OPEN_WITH} ")
                append(getUnlockClickable())
            })
        }
        else -> sender.sendFWDMessage(Strings.ONLY_LEADER_MAY_CLOSE_PARTY)
    }

    return true
}