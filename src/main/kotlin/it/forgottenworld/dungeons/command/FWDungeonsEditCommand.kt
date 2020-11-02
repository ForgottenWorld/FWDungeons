package it.forgottenworld.dungeons.command

import it.forgottenworld.dungeons.command.api.BranchingCommand
import it.forgottenworld.dungeons.command.api.PlayerCommand
import it.forgottenworld.dungeons.command.api.TreeCommand
import it.forgottenworld.dungeons.command.edit.activearea.*
import it.forgottenworld.dungeons.command.edit.dungeon.*
import it.forgottenworld.dungeons.command.edit.trigger.*

class FWDungeonsEditCommand : TreeCommand(
        "fwdungeonsedit",
        BranchingCommand(mapOf(
            *(BranchingCommand(mapOf(
                    "pos1" to PlayerCommand(::cmdActiveAreaPos1),
                    "pos2" to PlayerCommand(::cmdActiveAreaPos2),
                    "unmake" to PlayerCommand(::cmdActiveAreaUnmake),
                    "label" to PlayerCommand(::cmdActiveAreaLabel),
                    "wand" to PlayerCommand(::cmdActiveAreaWand),
                    "list" to PlayerCommand(::cmdActiveAreaList),
                    "hl" to PlayerCommand(::cmdActiveAreaHl)
            )).let{ arrayOf("activearea" to it, "aa" to it) }),
            *(BranchingCommand(mapOf(
                    "pos1" to PlayerCommand(::cmdTriggerPos1),
                    "pos2" to PlayerCommand(::cmdTriggerPos2),
                    "unmake" to PlayerCommand(::cmdTriggerUnmake),
                    "label" to PlayerCommand(::cmdTriggerLabel),
                    "wand" to PlayerCommand(::cmdTriggerWand),
                    "list" to PlayerCommand(::cmdTriggerList),
                    "hl" to PlayerCommand(::cmdTriggerHl)
            )).let{ arrayOf("trigger" to it, "t" to it) }),
            *(BranchingCommand(mapOf(
                    "create" to PlayerCommand(::cmdDungeonCreate),
                    "edit" to PlayerCommand(::cmdDungeonEdit),
                    "name" to PlayerCommand(::cmdDungeonName),
                    "pos1" to PlayerCommand(::cmdDungeonPos1),
                    "pos2" to PlayerCommand(::cmdDungeonPos2),
                    "instadd" to PlayerCommand(::cmdDungeonInstanceAdd),
                    "instremove" to PlayerCommand(::cmdDungeonInstanceRemove),
                    "writeout" to PlayerCommand(::cmdDungeonWriteOut),
                    "setstart" to PlayerCommand(::cmdDungeonSetStart),
                    "discard" to PlayerCommand(::cmdDungeonDiscard),
                    "difficulty" to PlayerCommand(::cmdDungeonDifficulty),
                    "description" to PlayerCommand(::cmdDungeonDescription),
                    "players" to PlayerCommand(::cmdDungeonNumberOfPlayers),
                    "save" to PlayerCommand(::cmdDungeonSave),
                    "points" to PlayerCommand(::cmdDungeonPoints),
                    "hlframes" to PlayerCommand(::cmdDungeonHlFrames)
            )).let{ arrayOf("dungeon" to it, "d" to it) })
    )))