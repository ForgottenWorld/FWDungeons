package it.forgottenworld.dungeons.command.play

import it.forgottenworld.dungeons.controller.FWDungeonsEditController
import it.forgottenworld.dungeons.utils.getTargetBlock
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

val dungeonCmdBindings: Map<String, (CommandSender, Command, String, Array<String>) -> Boolean> =
        mapOf(
                "newinst" to ::cmdDungeonCreateInstance,
                "join" to ::cmdDungeonJoinInstance
        )

fun cmdDungeonCreateInstance(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
    if (sender is Player) {

    }
    return true
}

fun cmdDungeonJoinInstance(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
    if (sender is Player) {

    }
    return true
}