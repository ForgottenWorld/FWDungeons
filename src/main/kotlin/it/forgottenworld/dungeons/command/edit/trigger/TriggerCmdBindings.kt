@file:Suppress("UNUSED_PARAMETER")

package it.forgottenworld.dungeons.command.edit.trigger

import org.bukkit.command.Command
import org.bukkit.command.CommandSender


val triggerCmdBindings: Map<String, (CommandSender, Command, String, Array<String>) -> Boolean> =
        mapOf(
                "pos1" to ::cmdTriggerPos1,
                "pos2" to ::cmdTriggerPos2,
                "unmake" to ::cmdTriggerUnmake,
                "label" to ::cmdTriggerLabel,
                "wand" to ::cmdTriggerWand
        )


