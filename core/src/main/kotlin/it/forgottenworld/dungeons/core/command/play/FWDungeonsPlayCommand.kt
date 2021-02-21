package it.forgottenworld.dungeons.core.command.play

import it.forgottenworld.dungeons.api.command.BranchingCommand
import it.forgottenworld.dungeons.api.command.TreeCommand
import it.forgottenworld.dungeons.core.command.play.dungeon.CmdDungeonDisable
import it.forgottenworld.dungeons.core.command.play.dungeon.CmdDungeonEnable
import it.forgottenworld.dungeons.core.command.play.dungeon.CmdDungeonEvacuate
import it.forgottenworld.dungeons.core.command.play.dungeon.CmdDungeonInvite
import it.forgottenworld.dungeons.core.command.play.dungeon.CmdDungeonJoinInstance
import it.forgottenworld.dungeons.core.command.play.dungeon.CmdDungeonLeave
import it.forgottenworld.dungeons.core.command.play.dungeon.CmdDungeonList
import it.forgottenworld.dungeons.core.command.play.dungeon.CmdDungeonLockParty
import it.forgottenworld.dungeons.core.command.play.dungeon.CmdDungeonLookup
import it.forgottenworld.dungeons.core.command.play.dungeon.CmdDungeonReload
import it.forgottenworld.dungeons.core.command.play.dungeon.CmdDungeonStart
import it.forgottenworld.dungeons.core.command.play.dungeon.CmdDungeonUnlockParty

class FWDungeonsPlayCommand : TreeCommand(
    "fwdungeons",
    BranchingCommand(
        mapOf(
            "join" to CmdDungeonJoinInstance(),
            "list" to CmdDungeonList(),
            "invite" to CmdDungeonInvite(),
            "leave" to CmdDungeonLeave(),
            "lock" to CmdDungeonLockParty(),
            "unlock" to CmdDungeonUnlockParty(),
            "start" to CmdDungeonStart(),
            "evacuate" to CmdDungeonEvacuate(),
            "lookup" to CmdDungeonLookup(),
            "enable" to CmdDungeonEnable(),
            "disable" to CmdDungeonDisable(),
            "reload" to CmdDungeonReload()
        )
    )
)