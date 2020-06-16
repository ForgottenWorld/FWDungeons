@file:Suppress("UNUSED_PARAMETER")

package it.forgottenworld.dungeons.command.edit

import it.forgottenworld.dungeons.controller.FWDungeonsEditController
import it.forgottenworld.dungeons.cui.StringConst
import it.forgottenworld.dungeons.cui.getString
import it.forgottenworld.dungeons.utils.getTargetBlock
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@ExperimentalStdlibApi
val triggerCmdBindings: Map<String, (CommandSender, Command, String, Array<String>) -> Boolean> =
        mapOf(
                "pos1" to ::cmdTriggerPos1,
                "pos2" to ::cmdTriggerPos2,
                "unmake" to ::cmdTriggerUnmake,
                "label" to ::cmdTriggerLabel
        )


fun cmdTriggerPos1(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
    if (sender is Player) {
        val block = sender.getTargetBlock()

        if (block == null) {
            sender.sendMessage("${getString(StringConst.CHAT_PREFIX)}You need to be targeting a block within 5 blocks of you before calling this")
            return true
        }

        val ret = FWDungeonsEditController.playerSetTriggerPos1(sender, block)
        sender.sendMessage( getString(StringConst.CHAT_PREFIX) +
                when (ret) {
                    -1 -> "You're not editing any dungeons"
                    -2 -> "First position set, now pick another with /fwde trigger pos2"
                    -3 -> "Dungeon box should be set before adding triggers"
                    -4 -> "Target is not inside the dungeon box"
                    -5 -> "This dungeon was already exported beforehand"
                    else -> "Created trigger with id $ret"
                }
        )
    }
    return true
}

fun cmdTriggerPos2(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
    if (sender is Player) {
        val block = sender.getTargetBlock()

        if (block == null) {
            sender.sendMessage("${getString(StringConst.CHAT_PREFIX)}You need to be targeting a block within 5 blocks of you before calling this")
            return true
        }

        val ret = FWDungeonsEditController.playerSetTriggerPos2(sender, block)
        sender.sendMessage( getString(StringConst.CHAT_PREFIX) +
                when (ret) {
                    -1 -> "You're not editing any dungeons"
                    -2 -> "First position set, now pick another with /fwde trigger pos1"
                    -3 -> "Dungeon box should be set before adding triggers"
                    -4 -> "Target is not inside the dungeon box"
                    -5 -> "This dungeon was already exported beforehand"
                    else -> "Created trigger with id $ret"
                }
        )
    }
    return true
}

@ExperimentalStdlibApi
fun cmdTriggerUnmake(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
    if (sender is Player) {

        val ret = FWDungeonsEditController.playerUnmakeTrigger(sender)
        sender.sendMessage( getString(StringConst.CHAT_PREFIX) +
                when (ret) {
                    -1 -> "You're not editing any dungeons"
                    -2 -> "This dungeon has no triggers yet"
                    -3 -> "This dungeon was already exported beforehand"
                    else -> "Deleted trigger with id $ret"
                }
        )
    }
    return true
}

fun cmdTriggerLabel(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
    if (sender is Player) {
        val tLabel = args.joinToString(" ").trim()

        if (tLabel.isEmpty()) {
            sender.sendMessage("${getString(StringConst.CHAT_PREFIX)}Not enough arguments: please provide a non-whitespace only label")
            return true
        }

        val ret = FWDungeonsEditController.playerLabelTrigger(sender, tLabel)
        sender.sendMessage( getString(StringConst.CHAT_PREFIX) +
                when (ret) {
                    -1 -> "You're not editing any dungeons"
                    -2 -> "This dungeon has no triggers yet"
                    -3 -> "This dungeon was already exported beforehand"
                    0 -> "Set label $tLabel"
                    else -> ""
                }
        )
    }
    return true
}