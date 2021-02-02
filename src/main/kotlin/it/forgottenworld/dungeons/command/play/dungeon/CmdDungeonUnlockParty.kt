package it.forgottenworld.dungeons.command.play.dungeon

import it.forgottenworld.dungeons.config.Strings
import it.forgottenworld.dungeons.cli.getLockClickable
import it.forgottenworld.dungeons.model.instance.DungeonFinalInstance.Companion.finalInstance
import it.forgottenworld.dungeons.utils.chatComponent
import it.forgottenworld.dungeons.utils.sendFWDMessage
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

fun cmdDungeonUnlockParty(sender: CommandSender, args: Array<out String>): Boolean {
    if (sender !is Player) return true

    val instance = sender.finalInstance ?: run {
        sender.sendFWDMessage(Strings.CURRENTLY_NOT_IN_DUNGEON_PARTY)
        return true
    }

    when {
        !instance.isLocked -> sender.sendFWDMessage(Strings.DUNGEON_PARTY_ALREADY_PUBLIC)
        sender == instance.leader -> {
            instance.unlock()
            sender.spigot().sendMessage(*chatComponent {
                append("${Strings.CHAT_PREFIX}The dungeon party is now public, anyone can join. To make it private, click ")
                append(getLockClickable())
            })
        }
        else -> sender.sendFWDMessage(Strings.ONLY_LEADER_MAY_OPEN_PARTY)
    }

    return true
}