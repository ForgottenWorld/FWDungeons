package it.forgottenworld.dungeons.command.edit

import it.forgottenworld.dungeons.controller.FWDungeonsEditController
import it.forgottenworld.dungeons.utils.getTargetBlock
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

val triggerCmdBindings: Map<String, (CommandSender, Command, String, Array<String>) -> Boolean> =
        mapOf(
                "pos1" to ::cmdTriggerPos1,
                "pos2" to ::cmdTriggerPos2
        )


fun cmdTriggerPos1(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
    if (sender is Player) {
        val block = sender.getTargetBlock()

        if (block == null) {
            sender.sendMessage("You need to be targeting a block within 5 blocks of you before calling this")
            return true
        }

        val ret = FWDungeonsEditController.playerSetTriggerPos1(sender, block)
        sender.sendMessage(
                when (ret) {
                    -1 -> "You're not editing any dungeons"
                    -2 -> "First position set, now pick another with /fwde trigger pos2"
                    -3 -> "Dungeon box should be set before adding triggers"
                    -4 -> "Target is not inside the dungeon box"
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
            sender.sendMessage("You need to be targeting a block within 5 blocks of you before calling this")
            return true
        }

        val ret = FWDungeonsEditController.playerSetTriggerPos2(sender, block)
        sender.sendMessage(
                when (ret) {
                    -1 -> "You're not editing any dungeons"
                    -2 -> "First position set, now pick another with /fwde trigger pos1"
                    -3 -> "Dungeon box should be set before adding triggers"
                    -4 -> "Target is not inside the dungeon box"
                    else -> "Created trigger with id $ret"
                }
        )
    }
    return true
}