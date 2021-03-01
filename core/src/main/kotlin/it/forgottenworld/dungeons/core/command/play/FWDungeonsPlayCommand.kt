package it.forgottenworld.dungeons.core.command.play

import com.google.inject.Inject
import it.forgottenworld.dungeons.api.command.BranchingCommand
import it.forgottenworld.dungeons.api.command.TreeCommand
import it.forgottenworld.dungeons.core.command.play.dungeon.*

class FWDungeonsPlayCommand @Inject constructor(
    cmdDungeonJoinInstance: CmdDungeonJoinInstance,
    cmdDungeonList: CmdDungeonList,
    cmdDungeonInvite: CmdDungeonInvite,
    cmdDungeonLeave: CmdDungeonLeave,
    cmdDungeonLockParty: CmdDungeonLockParty,
    cmdDungeonUnlockParty: CmdDungeonUnlockParty,
    cmdDungeonStart: CmdDungeonStart,
    cmdDungeonEvacuate: CmdDungeonEvacuate,
    cmdDungeonLookup: CmdDungeonLookup,
    cmdDungeonEnable: CmdDungeonEnable,
    cmdDungeonDisable: CmdDungeonDisable,
    cmdDungeonReload: CmdDungeonReload
) : TreeCommand(
    "fwdungeons",
    BranchingCommand(
        mapOf(
            "join" to cmdDungeonJoinInstance,
            "list" to cmdDungeonList,
            "invite" to cmdDungeonInvite,
            "leave" to cmdDungeonLeave,
            "lock" to cmdDungeonLockParty,
            "unlock" to cmdDungeonUnlockParty,
            "start" to cmdDungeonStart,
            "evacuate" to cmdDungeonEvacuate,
            "lookup" to cmdDungeonLookup,
            "enable" to cmdDungeonEnable,
            "disable" to cmdDungeonDisable,
            "reload" to cmdDungeonReload
        )
    )
)