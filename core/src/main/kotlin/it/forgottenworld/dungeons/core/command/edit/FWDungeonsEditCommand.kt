package it.forgottenworld.dungeons.core.command.edit

import com.google.inject.Inject
import it.forgottenworld.dungeons.api.command.BranchingCommand
import it.forgottenworld.dungeons.api.command.TreeCommand
import it.forgottenworld.dungeons.core.command.edit.activearea.*
import it.forgottenworld.dungeons.core.command.edit.chest.CmdChestAdd
import it.forgottenworld.dungeons.core.command.edit.chest.CmdChestLabel
import it.forgottenworld.dungeons.core.command.edit.chest.CmdChestRemove
import it.forgottenworld.dungeons.core.command.edit.dungeon.*
import it.forgottenworld.dungeons.core.command.edit.trigger.*

class FWDungeonsEditCommand @Inject constructor(
    cmdActiveAreaPos1: CmdActiveAreaPos1,
    cmdActiveAreaPos2: CmdActiveAreaPos2,
    cmdActiveAreaUnmake: CmdActiveAreaUnmake,
    cmdActiveAreaLabel: CmdActiveAreaLabel,
    cmdActiveAreaWand: CmdActiveAreaWand,
    cmdActiveAreaList: CmdActiveAreaList,
    cmdActiveAreaHl: CmdActiveAreaHl,
    cmdTriggerPos1: CmdTriggerPos1,
    cmdTriggerPos2: CmdTriggerPos2,
    cmdTriggerUnmake: CmdTriggerUnmake,
    cmdTriggerLabel: CmdTriggerLabel,
    cmdTriggerWand: CmdTriggerWand,
    cmdTriggerList: CmdTriggerList,
    cmdTriggerHl: CmdTriggerHl,
    cmdTriggerCode: CmdTriggerCode,
    cmdChestAdd: CmdChestAdd,
    cmdChestRemove: CmdChestRemove,
    cmdChestLabel: CmdChestLabel,
    cmdDungeonCreate: CmdDungeonCreate,
    cmdDungeonEdit: CmdDungeonEdit,
    cmdDungeonName: CmdDungeonName,
    cmdDungeonPos1: CmdDungeonPos1,
    cmdDungeonPos2: CmdDungeonPos2,
    cmdDungeonInstanceAdd: CmdDungeonInstanceAdd,
    cmdDungeonInstanceRemove: CmdDungeonInstanceRemove,
    cmdDungeonWriteOut: CmdDungeonWriteOut,
    cmdDungeonSetStart: CmdDungeonSetStart,
    cmdDungeonDiscard: CmdDungeonDiscard,
    cmdDungeonDifficulty: CmdDungeonDifficulty,
    cmdDungeonDescription: CmdDungeonDescription,
    cmdDungeonNumberOfPlayers: CmdDungeonNumberOfPlayers,
    cmdDungeonSave: CmdDungeonSave,
    cmdDungeonPoints: CmdDungeonPoints,
    cmdDungeonHlFrames: CmdDungeonHlFrames,
    cmdDungeonVolumeMap: CmdDungeonVolumeMap,
    cmdDungeonImport: CmdDungeonImport
) : TreeCommand(
    "fwdungeonsedit",
    BranchingCommand(
        mapOf(
            *(BranchingCommand(
                mapOf(
                    "pos1" to cmdActiveAreaPos1,
                    "pos2" to cmdActiveAreaPos2,
                    "unmake" to cmdActiveAreaUnmake,
                    "label" to cmdActiveAreaLabel,
                    "wand" to cmdActiveAreaWand,
                    "list" to cmdActiveAreaList,
                    "hl" to cmdActiveAreaHl
                )
            ).let {
                arrayOf(
                    "activearea" to it,
                    "aa" to it
                )
            }),
            *(BranchingCommand(
                mapOf(
                    "pos1" to cmdTriggerPos1,
                    "pos2" to cmdTriggerPos2,
                    "unmake" to cmdTriggerUnmake,
                    "label" to cmdTriggerLabel,
                    "wand" to cmdTriggerWand,
                    "list" to cmdTriggerList,
                    "hl" to cmdTriggerHl,
                    "code" to cmdTriggerCode
                )
            ).let {
                arrayOf(
                    "trigger" to it,
                    "t" to it
                )
            }),
            *(BranchingCommand(
                mapOf(
                    "add" to cmdChestAdd,
                    "remove" to cmdChestRemove,
                    "label" to cmdChestLabel
                )
            ).let {
                arrayOf(
                    "chest" to it,
                    "c" to it
                )
            }),
            *(BranchingCommand(
                mapOf(
                    "create" to cmdDungeonCreate,
                    "edit" to cmdDungeonEdit,
                    "name" to cmdDungeonName,
                    "pos1" to cmdDungeonPos1,
                    "pos2" to cmdDungeonPos2,
                    "instadd" to cmdDungeonInstanceAdd,
                    "instremove" to cmdDungeonInstanceRemove,
                    "writeout" to cmdDungeonWriteOut,
                    "setstart" to cmdDungeonSetStart,
                    "discard" to cmdDungeonDiscard,
                    "difficulty" to cmdDungeonDifficulty,
                    "description" to cmdDungeonDescription,
                    "players" to cmdDungeonNumberOfPlayers,
                    "save" to cmdDungeonSave,
                    "points" to cmdDungeonPoints,
                    "hlframes" to cmdDungeonHlFrames,
                    "volmap" to cmdDungeonVolumeMap,
                    "import" to cmdDungeonImport
                )
            ).let {
                arrayOf(
                    "dungeon" to it,
                    "d" to it
                )
            })
        )
    )
)