package it.forgottenworld.dungeons.command

import it.forgottenworld.dungeons.command.api.BranchingCommand
import it.forgottenworld.dungeons.command.api.PlayerCommand
import it.forgottenworld.dungeons.command.api.SenderCommand
import it.forgottenworld.dungeons.command.api.TreeCommand
import it.forgottenworld.dungeons.command.play.dungeon.*

class FWDungeonsCommand : TreeCommand(
        "fwdungeons",
        BranchingCommand(mapOf(
                "join" to PlayerCommand(::cmdDungeonJoinInstance),
                "list" to PlayerCommand(::cmdDungeonList),
                "invite" to PlayerCommand(::cmdDungeonInvite),
                "leave" to PlayerCommand(::cmdDungeonLeave),
                "lock" to PlayerCommand(::cmdDungeonLockParty),
                "unlock" to PlayerCommand(::cmdDungeonUnlockParty),
                "start" to PlayerCommand(::cmdDungeonStart),
                "evacuate" to SenderCommand(::cmdDungeonEvacuate),
                "lookup" to SenderCommand(::cmdDungeonPlayerLookup),
                "enable" to SenderCommand(::cmdDungeonEnable),
                "disable" to SenderCommand(::cmdDungeonDisable),
                "reload" to SenderCommand(::cmdDungeonReload),
        )))