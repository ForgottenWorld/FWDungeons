package it.forgottenworld.dungeons.command.edit

import it.forgottenworld.dungeons.controller.FWDungeonsEditController
import it.forgottenworld.dungeons.utils.getTargetBlock
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

val activeAreaCmdBindings: Map<String, (CommandSender, Command, String, Array<String>) -> Boolean> =
        mapOf(
                "pos1" to ::cmdActiveAreaPos1,
                "pos2" to ::cmdActiveAreaPos2
        )


fun cmdActiveAreaPos1(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
    if (sender is Player) {
        val block = sender.getTargetBlock()

        if (block == null) {
            sender.sendMessage("You need to be targeting a block within 5 blocks of you before calling this")
            return true
        }

        val ret = FWDungeonsEditController.playerSetActiveAreaPos1(sender, block)
        sender.sendMessage(
                when (ret) {
                    -1 -> "You're not editing any dungeons"
                    -2 -> "First position set, now pick another with /fwde activearea pos2"
                    -3 -> "Dungeon box should be set before adding active areas"
                    -4 -> "Target is not inside the dungeon box"
                    else -> "Created active area with id $ret"
                }
        )
    }
    return true
}

fun cmdActiveAreaPos2(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
    if (sender is Player) {
        val block = sender.getTargetBlock()

        if (block == null) {
            sender.sendMessage("You need to be targeting a block within 5 blocks of you before calling this")
            return true
        }

        val ret = FWDungeonsEditController.playerSetActiveAreaPos2(sender, block)
        sender.sendMessage(
                when (ret) {
                    -1 -> "You're not editing any dungeons"
                    -2 -> "First position set, now pick another with /fwde activearea pos1"
                    -3 -> "Dungeon box should be set before adding active areas"
                    -4 -> "Target is not inside the dungeon box"
                    else -> "Created active area with id $ret"
                }
        )
    }
    return true
}