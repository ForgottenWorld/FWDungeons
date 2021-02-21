package it.forgottenworld.dungeons.core.command.edit

import it.forgottenworld.dungeons.api.command.BranchingCommand
import it.forgottenworld.dungeons.api.command.TreeCommand
import it.forgottenworld.dungeons.core.command.edit.activearea.CmdActiveAreaHl
import it.forgottenworld.dungeons.core.command.edit.activearea.CmdActiveAreaLabel
import it.forgottenworld.dungeons.core.command.edit.activearea.CmdActiveAreaList
import it.forgottenworld.dungeons.core.command.edit.activearea.CmdActiveAreaPos1
import it.forgottenworld.dungeons.core.command.edit.activearea.CmdActiveAreaPos2
import it.forgottenworld.dungeons.core.command.edit.activearea.CmdActiveAreaUnmake
import it.forgottenworld.dungeons.core.command.edit.activearea.CmdActiveAreaWand
import it.forgottenworld.dungeons.core.command.edit.chest.CmdChestAdd
import it.forgottenworld.dungeons.core.command.edit.chest.CmdChestLabel
import it.forgottenworld.dungeons.core.command.edit.chest.CmdChestRemove
import it.forgottenworld.dungeons.core.command.edit.dungeon.CmdDungeonCreate
import it.forgottenworld.dungeons.core.command.edit.dungeon.CmdDungeonDescription
import it.forgottenworld.dungeons.core.command.edit.dungeon.CmdDungeonDifficulty
import it.forgottenworld.dungeons.core.command.edit.dungeon.CmdDungeonDiscard
import it.forgottenworld.dungeons.core.command.edit.dungeon.CmdDungeonEdit
import it.forgottenworld.dungeons.core.command.edit.dungeon.CmdDungeonHlFrames
import it.forgottenworld.dungeons.core.command.edit.dungeon.CmdDungeonImport
import it.forgottenworld.dungeons.core.command.edit.dungeon.CmdDungeonInstanceAdd
import it.forgottenworld.dungeons.core.command.edit.dungeon.CmdDungeonInstanceRemove
import it.forgottenworld.dungeons.core.command.edit.dungeon.CmdDungeonName
import it.forgottenworld.dungeons.core.command.edit.dungeon.CmdDungeonNumberOfPlayers
import it.forgottenworld.dungeons.core.command.edit.dungeon.CmdDungeonPoints
import it.forgottenworld.dungeons.core.command.edit.dungeon.CmdDungeonPos1
import it.forgottenworld.dungeons.core.command.edit.dungeon.CmdDungeonPos2
import it.forgottenworld.dungeons.core.command.edit.dungeon.CmdDungeonSave
import it.forgottenworld.dungeons.core.command.edit.dungeon.CmdDungeonSetStart
import it.forgottenworld.dungeons.core.command.edit.dungeon.CmdDungeonVolumeMap
import it.forgottenworld.dungeons.core.command.edit.dungeon.CmdDungeonWriteOut
import it.forgottenworld.dungeons.core.command.edit.trigger.CmdTriggerCode
import it.forgottenworld.dungeons.core.command.edit.trigger.CmdTriggerHl
import it.forgottenworld.dungeons.core.command.edit.trigger.CmdTriggerLabel
import it.forgottenworld.dungeons.core.command.edit.trigger.CmdTriggerList
import it.forgottenworld.dungeons.core.command.edit.trigger.CmdTriggerPos1
import it.forgottenworld.dungeons.core.command.edit.trigger.CmdTriggerPos2
import it.forgottenworld.dungeons.core.command.edit.trigger.CmdTriggerUnmake
import it.forgottenworld.dungeons.core.command.edit.trigger.CmdTriggerWand

class FWDungeonsEditCommand : TreeCommand(
    "fwdungeonsedit",
    BranchingCommand(
        mapOf(
            *(BranchingCommand(
                mapOf(
                    "pos1" to CmdActiveAreaPos1(),
                    "pos2" to CmdActiveAreaPos2(),
                    "unmake" to CmdActiveAreaUnmake(),
                    "label" to CmdActiveAreaLabel(),
                    "wand" to CmdActiveAreaWand(),
                    "list" to CmdActiveAreaList(),
                    "hl" to CmdActiveAreaHl()
                )
            ).let {
                arrayOf(
                    "activearea" to it,
                    "aa" to it
                )
            }),
            *(BranchingCommand(
                mapOf(
                    "pos1" to CmdTriggerPos1(),
                    "pos2" to CmdTriggerPos2(),
                    "unmake" to CmdTriggerUnmake(),
                    "label" to CmdTriggerLabel(),
                    "wand" to CmdTriggerWand(),
                    "list" to CmdTriggerList(),
                    "hl" to CmdTriggerHl(),
                    "code" to CmdTriggerCode()
                )
            ).let {
                arrayOf(
                    "trigger" to it,
                    "t" to it
                )
            }),
            *(BranchingCommand(
                mapOf(
                    "add" to CmdChestAdd(),
                    "remove" to CmdChestRemove(),
                    "label" to CmdChestLabel()
                )
            ).let {
                arrayOf(
                    "chest" to it,
                    "c" to it
                )
            }),
            *(BranchingCommand(
                mapOf(
                    "create" to CmdDungeonCreate(),
                    "edit" to CmdDungeonEdit(),
                    "name" to CmdDungeonName(),
                    "pos1" to CmdDungeonPos1(),
                    "pos2" to CmdDungeonPos2(),
                    "instadd" to CmdDungeonInstanceAdd(),
                    "instremove" to CmdDungeonInstanceRemove(),
                    "writeout" to CmdDungeonWriteOut(),
                    "setstart" to CmdDungeonSetStart(),
                    "discard" to CmdDungeonDiscard(),
                    "difficulty" to CmdDungeonDifficulty(),
                    "description" to CmdDungeonDescription(),
                    "players" to CmdDungeonNumberOfPlayers(),
                    "save" to CmdDungeonSave(),
                    "points" to CmdDungeonPoints(),
                    "hlframes" to CmdDungeonHlFrames(),
                    "volmap" to CmdDungeonVolumeMap(),
                    "import" to CmdDungeonImport()
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