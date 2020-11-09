package it.forgottenworld.dungeons.command.play.dungeon

import it.forgottenworld.dungeons.cli.Strings
import it.forgottenworld.dungeons.cli.getUnlockClickable
import it.forgottenworld.dungeons.model.instance.DungeonFinalInstance.Companion.finalInstance
import it.forgottenworld.dungeons.utils.ktx.component
import it.forgottenworld.dungeons.utils.ktx.sendFWDMessage
import org.bukkit.entity.Player

fun cmdDungeonLockParty(sender: Player, args: Array<out String>): Boolean {
    val instance = sender.finalInstance ?: run {
        sender.sendFWDMessage("You're currently not in a dungeon party")
        return true
    }

    when {
        instance.isLocked -> sender.sendFWDMessage("This dungeon party is already private")
        sender == instance.leader -> {
            instance.lock()
            sender.spigot().sendMessage(*component {
                append("${Strings.CHAT_PREFIX}The dungeon party is now private, invite players with /fwd invite. To make it public, click ")
                append(getUnlockClickable())
            })
        }
        else -> sender.sendFWDMessage("Only the dungeon party leader may make the party private")
    }

    return true
}