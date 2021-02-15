package it.forgottenworld.dungeons.command.play

import it.forgottenworld.dungeons.command.api.BranchingCommand
import it.forgottenworld.dungeons.command.api.PlayerCommand
import it.forgottenworld.dungeons.command.api.SenderCommand
import it.forgottenworld.dungeons.command.api.TreeCommand
import it.forgottenworld.dungeons.command.play.dungeon.*

class FWDungeonsPlayCommand : TreeCommand(
    "fwdungeons",
    BranchingCommand(
        mapOf(
            "join" to PlayerCommand(CmdDungeonJoinInstance()),
            "list" to PlayerCommand(CmdDungeonList()),
            "invite" to PlayerCommand(CmdDungeonInvite()),
            "leave" to PlayerCommand(CmdDungeonLeave()),
            "lock" to PlayerCommand(CmdDungeonLockParty()),
            "unlock" to PlayerCommand(CmdDungeonUnlockParty()),
            "start" to PlayerCommand(CmdDungeonStart()),
            "evacuate" to SenderCommand(CmdDungeonEvacuate()),
            "lookup" to SenderCommand(CmdDungeonLookup()),
            "enable" to SenderCommand(CmdDungeonEnable()),
            "disable" to SenderCommand(CmdDungeonDisable()),
            "reload" to SenderCommand(CmdDungeonReload()),
        )
    )
)