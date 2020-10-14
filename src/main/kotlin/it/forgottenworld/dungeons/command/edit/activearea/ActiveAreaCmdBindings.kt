@file:Suppress("UNUSED_PARAMETER")

package it.forgottenworld.dungeons.command.edit.activearea

import org.bukkit.command.Command
import org.bukkit.command.CommandSender


val activeAreaCmdBindings: Map<String, (CommandSender, Command, String, Array<String>) -> Boolean> =
        mapOf(
                "pos1" to ::cmdActiveAreaPos1,
                "pos2" to ::cmdActiveAreaPos2,
                "unmake" to ::cmdActiveAreaUnmake,
                "label" to ::cmdActiveAreaLabel,
                "wand" to ::cmdActiveAreaWand
        )


