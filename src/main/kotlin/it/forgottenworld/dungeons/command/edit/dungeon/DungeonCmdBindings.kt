@file:Suppress("UNUSED_PARAMETER")

package it.forgottenworld.dungeons.command.edit.dungeon

import org.bukkit.command.Command
import org.bukkit.command.CommandSender

val dungeonCmdBindings: Map<String, (CommandSender, Command, String, Array<String>) -> Boolean> =
        mapOf(
                "create" to ::cmdDungeonCreate,
                "edit" to ::cmdDungeonEdit,
                "name" to ::cmdDungeonName,
                "pos1" to ::cmdDungeonPos1,
                "pos2" to ::cmdDungeonPos2,
                "instadd" to ::cmdDungeonInstanceAdd,
                "instremove" to ::cmdDungeonInstanceRemove,
                "writeout" to ::cmdDungeonWriteOut,
                "setstart" to ::cmdDungeonSetStart,
                "discard" to ::cmdDungeonDiscard,
                "difficulty" to ::cmdDungeonDifficulty,
                "description" to ::cmdDungeonDescription,
                "players" to ::cmdDungeonNumberOfPlayers,
                "save" to ::cmdDungeonSave,
                "points" to ::cmdDungeonPoints,
                "hlframes" to ::cmdDungeonHlFrames
        )


