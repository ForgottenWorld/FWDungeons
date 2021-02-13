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
            "pos1" to PlayerCommand(CmdActiveAreaPos1()),
            "pos2" to PlayerCommand(CmdActiveAreaPos2()),
            "unmake" to PlayerCommand(CmdActiveAreaUnmake()),
            "label" to PlayerCommand(CmdActiveAreaLabel()),
            "wand" to PlayerCommand(CmdActiveAreaWand()),
            "list" to PlayerCommand(CmdActiveAreaList()),
            "hl" to PlayerCommand(CmdActiveAreaHl())
        )).let { arrayOf("activearea" to it, "aa" to it) }),
        *(BranchingCommand(mapOf(
            "pos1" to PlayerCommand(CmdTriggerPos1()),
            "pos2" to PlayerCommand(CmdTriggerPos2()),
            "unmake" to PlayerCommand(CmdTriggerUnmake()),
            "label" to PlayerCommand(CmdTriggerLabel()),
            "wand" to PlayerCommand(CmdTriggerWand()),
            "list" to PlayerCommand(CmdTriggerList()),
            "hl" to PlayerCommand(CmdTriggerHl()),
            "code" to PlayerCommand(CmdTriggerCode())
        )).let { arrayOf("trigger" to it, "t" to it) }),
        *(BranchingCommand(mapOf(
            "create" to PlayerCommand(CmdDungeonCreate()),
            "edit" to PlayerCommand(CmdDungeonEdit()),
            "name" to PlayerCommand(CmdDungeonName()),
            "pos1" to PlayerCommand(CmdDungeonPos1()),
            "pos2" to PlayerCommand(CmdDungeonPos2()),
            "instadd" to PlayerCommand(CmdDungeonInstanceAdd()),
            "instremove" to PlayerCommand(CmdDungeonInstanceRemove()),
            "writeout" to PlayerCommand(CmdDungeonWriteOut()),
            "setstart" to PlayerCommand(CmdDungeonSetStart()),
            "discard" to PlayerCommand(CmdDungeonDiscard()),
            "difficulty" to PlayerCommand(CmdDungeonDifficulty()),
            "description" to PlayerCommand(CmdDungeonDescription()),
            "players" to PlayerCommand(CmdDungeonNumberOfPlayers()),
            "save" to PlayerCommand(CmdDungeonSave()),
            "points" to PlayerCommand(CmdDungeonPoints()),
            "hlframes" to PlayerCommand(CmdDungeonHlFrames()),
            "volmap" to PlayerCommand(CmdDungeonVolumeMap()),
            "import" to PlayerCommand(CmdDungeonImport())
        )).let { arrayOf("dungeon" to it, "d" to it) })
    )))