package it.forgottenworld.dungeons.command.play.dungeon

import it.forgottenworld.dungeons.cli.Strings
import it.forgottenworld.dungeons.cli.getLockClickable
import it.forgottenworld.dungeons.model.instance.DungeonFinalInstance.Companion.finalInstance
import it.forgottenworld.dungeons.utils.ktx.component
import it.forgottenworld.dungeons.utils.ktx.sendFWDMessage
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

fun cmdDungeonUnlockParty(sender: CommandSender, args: Array<out String>): Boolean {
    if (sender !is Player) return true

    val instance = sender.finalInstance ?: run {
        sender.sendFWDMessage("You're currently not in a dungeon party")
        return true
    }

    when {
        !instance.isLocked -> sender.sendFWDMessage("This dungeon party is already public")
        sender == instance.leader -> {
            instance.unlock()
            sender.spigot().sendMessage(*component {
                append("${Strings.CHAT_PREFIX}The dungeon party is now public, anyone can join. To make it private, click ")
                append(getLockClickable())
            })
        }
        else -> sender.sendFWDMessage("Only the dungeon party leader may make the party public")
    }

    return true
}