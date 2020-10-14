@file:Suppress("UNUSED_PARAMETER")

package it.forgottenworld.dungeons.command.play.dungeon

import org.bukkit.command.Command
import org.bukkit.command.CommandSender

val cmdDungeonBindings: Map<String, (CommandSender, Command, String, Array<String>) -> Boolean> =
        mapOf(
                "join" to ::cmdDungeonJoinInstance,
                "list" to ::cmdDungeonList,
                "invite" to ::cmdDungeonInvite,
                "leave" to ::cmdDungeonLeave,
                "lock" to ::cmdDungeonLockParty,
                "unlock" to ::cmdDungeonUnlockParty,
                "start" to ::cmdDungeonStart,
                "evacuate" to ::cmdDungeonEvacuate,
                "lookup" to ::cmdDungeonPlayerLookup,
                "enable" to ::cmdDungeonEnable,
                "disable" to ::cmdDungeonDisable,
                "reload" to ::cmdDungeonReload
        )