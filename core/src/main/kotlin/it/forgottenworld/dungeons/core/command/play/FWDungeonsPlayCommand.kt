package it.forgottenworld.dungeons.core.command.play

import com.google.inject.Inject
import it.forgottenworld.dungeons.api.command.TreeCommand
import it.forgottenworld.dungeons.api.command.branchingCommand
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
    branchingCommand {
        "join" += cmdDungeonJoinInstance
        "list" += cmdDungeonList
        "invite" += cmdDungeonInvite
        "leave" += cmdDungeonLeave
        "lock" += cmdDungeonLockParty
        "unlock" += cmdDungeonUnlockParty
        "start" += cmdDungeonStart
        "evacuate" += cmdDungeonEvacuate
        "lookup" += cmdDungeonLookup
        "enable" += cmdDungeonEnable
        "disable" += cmdDungeonDisable
        "reload" += cmdDungeonReload
    }
)