package it.forgottenworld.dungeons.core.command.edit

import com.google.inject.Inject
import it.forgottenworld.dungeons.api.command.TreeCommand
import it.forgottenworld.dungeons.api.command.branchingCommand
import it.forgottenworld.dungeons.core.command.edit.activearea.*
import it.forgottenworld.dungeons.core.command.edit.chest.CmdChestAdd
import it.forgottenworld.dungeons.core.command.edit.chest.CmdChestLabel
import it.forgottenworld.dungeons.core.command.edit.chest.CmdChestList
import it.forgottenworld.dungeons.core.command.edit.chest.CmdChestRemove
import it.forgottenworld.dungeons.core.command.edit.dungeon.*
import it.forgottenworld.dungeons.core.command.edit.spawnarea.*
import it.forgottenworld.dungeons.core.command.edit.trigger.*
import it.forgottenworld.dungeons.core.command.edit.unlockables.CmdUnlockablesBindPlate
import it.forgottenworld.dungeons.core.command.edit.unlockables.CmdUnlockablesLookupPlate
import it.forgottenworld.dungeons.core.command.edit.unlockables.CmdUnlockablesUnbindPlate

class FWDungeonsEditCommand @Inject constructor(
    cmdActiveAreaPos1: CmdActiveAreaPos1,
    cmdActiveAreaPos2: CmdActiveAreaPos2,
    cmdActiveAreaUnmake: CmdActiveAreaUnmake,
    cmdActiveAreaLabel: CmdActiveAreaLabel,
    cmdActiveAreaWand: CmdActiveAreaWand,
    cmdActiveAreaList: CmdActiveAreaList,
    cmdActiveAreaHl: CmdActiveAreaHl,
    cmdSpawnAreaPos1: CmdSpawnAreaPos1,
    cmdSpawnAreaPos2: CmdSpawnAreaPos2,
    cmdSpawnAreaUnmake: CmdSpawnAreaUnmake,
    cmdSpawnAreaLabel: CmdSpawnAreaLabel,
    cmdSpawnAreaWand: CmdSpawnAreaWand,
    cmdSpawnAreaList: CmdSpawnAreaList,
    cmdSpawnAreaHl: CmdSpawnAreaHl,
    cmdTriggerPos1: CmdTriggerPos1,
    cmdTriggerPos2: CmdTriggerPos2,
    cmdTriggerUnmake: CmdTriggerUnmake,
    cmdTriggerLabel: CmdTriggerLabel,
    cmdTriggerWand: CmdTriggerWand,
    cmdTriggerList: CmdTriggerList,
    cmdTriggerHl: CmdTriggerHl,
    // cmdTriggerCode: CmdTriggerCode,
    cmdChestAdd: CmdChestAdd,
    cmdChestRemove: CmdChestRemove,
    cmdChestLabel: CmdChestLabel,
    cmdChestList: CmdChestList,
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
    cmdDungeonImport: CmdDungeonImport,
    cmdUnlockablesBindPlate: CmdUnlockablesBindPlate,
    cmdUnlockablesLookupPlate: CmdUnlockablesLookupPlate,
    cmdUnlockablesUnbindPlate: CmdUnlockablesUnbindPlate
) : TreeCommand(
    "fwdungeonsedit",
    branchingCommand {
        branchingCommand {
            "pos1" += cmdActiveAreaPos1
            "pos2" += cmdActiveAreaPos2
            "unmake" += cmdActiveAreaUnmake
            "label" += cmdActiveAreaLabel
            "wand" += cmdActiveAreaWand
            "list" += cmdActiveAreaList
            "hl" += cmdActiveAreaHl
        }.bindTo("activearea", "aa")

        branchingCommand {
            "pos1" += cmdSpawnAreaPos1
            "pos2" += cmdSpawnAreaPos2
            "unmake" += cmdSpawnAreaUnmake
            "label" += cmdSpawnAreaLabel
            "wand" += cmdSpawnAreaWand
            "list" += cmdSpawnAreaList
            "hl" += cmdSpawnAreaHl
        }.bindTo("spawnarea", "sa")

        branchingCommand {
            "pos1" += cmdTriggerPos1
            "pos2" += cmdTriggerPos2
            "unmake" += cmdTriggerUnmake
            "label" += cmdTriggerLabel
            "wand" += cmdTriggerWand
            "list" += cmdTriggerList
            "hl" += cmdTriggerHl
            // "code" += cmdTriggerCode
        }.bindTo("trigger", "t")

        branchingCommand {
            "add" += cmdChestAdd
            "remove" += cmdChestRemove
            "label" += cmdChestLabel
            "list" += cmdChestList
        }.bindTo("chest", "c")

        branchingCommand {
            "create" += cmdDungeonCreate
            "edit" += cmdDungeonEdit
            "name" += cmdDungeonName
            "pos1" += cmdDungeonPos1
            "pos2" += cmdDungeonPos2
            "instadd" += cmdDungeonInstanceAdd
            "instremove" += cmdDungeonInstanceRemove
            "writeout" += cmdDungeonWriteOut
            "setstart" += cmdDungeonSetStart
            "discard" += cmdDungeonDiscard
            "difficulty" += cmdDungeonDifficulty
            "description" += cmdDungeonDescription
            "players" += cmdDungeonNumberOfPlayers
            "save" += cmdDungeonSave
            "points" += cmdDungeonPoints
            "hlframes" += cmdDungeonHlFrames
            "volmap" += cmdDungeonVolumeMap
            "import" += cmdDungeonImport
        }.bindTo("dungeon", "d")

        branchingCommand {
            "bindplate" += cmdUnlockablesBindPlate
            "lookupplate" += cmdUnlockablesLookupPlate
            "unbindplate" += cmdUnlockablesUnbindPlate
        }.bindTo("unlockables", "unl")
    }
)