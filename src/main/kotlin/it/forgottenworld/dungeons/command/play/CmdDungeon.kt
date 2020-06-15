package it.forgottenworld.dungeons.command.play

import it.forgottenworld.dungeons.controller.FWDungeonsController
import it.forgottenworld.dungeons.cui.StringConst
import it.forgottenworld.dungeons.cui.getInteractiveDungeonList
import it.forgottenworld.dungeons.cui.getLockClickable
import it.forgottenworld.dungeons.cui.getString
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

val cmdBindings: Map<String, (CommandSender, Command, String, Array<String>) -> Boolean> =
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
                "disable" to ::cmdDungeonDisable
        )

fun cmdDungeonJoinInstance(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
    if (sender is Player) {
        if (args.count() < 2) {
            sender.sendMessage("${getString(StringConst.CHAT_PREFIX)}Please provide both a dungeon and instance id")
            return true
        }

        val dungeonId = args[0].toIntOrNull()
        val instanceId = args[1].toIntOrNull()

        if (dungeonId == null || instanceId == null) {
            sender.sendMessage("${getString(StringConst.CHAT_PREFIX)}Dungeon id and instance id should both be integers")
            return true
        }

        val partyKey = if (args.count() > 2) args[2] else ""
        when (FWDungeonsController.playerJoinInstance(sender, instanceId, dungeonId, partyKey)) {
            -1 -> sender.sendMessage("${getString(StringConst.CHAT_PREFIX)}Invalid dungeon id")
            -2 -> sender.sendMessage("${getString(StringConst.CHAT_PREFIX)}Invalid instance id")
            -3 -> sender.sendMessage("${getString(StringConst.CHAT_PREFIX)}You're already in a party")
            -4 -> sender.sendMessage("${getString(StringConst.CHAT_PREFIX)}This dungeon party is full")
            -5 -> sender.sendMessage("${getString(StringConst.CHAT_PREFIX)}This dungeon party is private and you were not invited")
            -6 -> sender.sendMessage("${getString(StringConst.CHAT_PREFIX)}Couldn't join dungeon party")
            -7 -> sender.sendMessage("${getString(StringConst.CHAT_PREFIX)}This dungeon party has already entered the dungeon")
            -8 -> sender.sendMessage("${getString(StringConst.CHAT_PREFIX)}This dugeons is disabled")
            0 -> sender.spigot().sendMessage(
                    TextComponent("${getString(StringConst.CHAT_PREFIX)}Dungeon party created. To make it private, click ")
                            .apply {
                                addExtra(getLockClickable())
                            })
            1 -> sender.sendMessage("${getString(StringConst.CHAT_PREFIX)}You joined the dungeon party")
        }
    }
    return true
}

fun cmdDungeonList(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
    if (sender is Player) {
        val page = if (args.count() != 0 && args[0].toIntOrNull() != null) args[0].toInt() else 0
        sender.spigot().sendMessage(getInteractiveDungeonList(page))
    }
    return true
}

fun cmdDungeonInvite(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
    if (sender is Player) {
        if (args.count() == 0) {
            sender.sendMessage("${getString(StringConst.CHAT_PREFIX)}Please provide the name of whomever you want invite")
            return true
        }

        sender.sendMessage( getString(StringConst.CHAT_PREFIX) +
                when (FWDungeonsController.playerSendInvite(sender, args[0])) {
                    -1 -> "You're currently not in a dungeon party"
                    -2 -> "Only the dungeon party leader may invite others to join"
                    -3 -> "No currently online player has this name"
                    0 -> "Invite sent!"
                    else -> ""
        })
    }
    return true
}

fun cmdDungeonLeave(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
    if (sender is Player) {
        sender.sendMessage( getString(StringConst.CHAT_PREFIX) +
                when (FWDungeonsController.playerLeaveParty(sender)) {
                    -1 -> "You're currently not in a dungeon party"
                    -2 -> "The instance has started, you can't leave now"
                    0 -> "You left the dungeon party"
                    else -> ""
        })
    }
    return true
}

fun cmdDungeonLockParty(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
    if (sender is Player) {
            sender.sendMessage( getString(StringConst.CHAT_PREFIX) +
                    when (FWDungeonsController.playerLockParty(sender)) {
                        -1 -> "You're currently not in a dungeon party"
                        -2 -> "Only the dungeon party leader may make the party private"
                        -3 -> "This dungeon party is already private"
                        0 -> "The dungeon party is now private, invite players with /fwd invite"
                        else -> ""
                    })
    }
    return true
}

fun cmdDungeonUnlockParty(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
    if (sender is Player) {
        sender.sendMessage( getString(StringConst.CHAT_PREFIX) +
                when (FWDungeonsController.playerUnlockParty(sender)) {
                    -1 -> "You're currently not in a dungeon party"
                    -2 -> "Only the dungeon party leader may make the party public"
                    -3 -> "This dungeon party is already public"
                    0 -> "This dungeon party is now public, anyone can join"
                    else -> ""
                })
    }
    return true
}

fun cmdDungeonStart(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
    if (sender is Player) {
        sender.sendMessage( getString(StringConst.CHAT_PREFIX) +
                when (FWDungeonsController.playerStart(sender)) {
                    -1 -> "You're currently not in a dungeon party"
                    -2 -> "Only the dungeon party leader may start the instance"
                    -3 -> "Not enough players for this dungeon"
                    0 -> "Dungeon party members have been teleported to the dungeon entrance"
                    else -> ""
                })
    }
    return true
}

fun cmdDungeonEvacuate(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
    if (args.count() < 2) {
        sender.sendMessage("${getString(StringConst.CHAT_PREFIX)}Please provide both a dungeon and instance id")
        return true
    }

    val dungeonId = args[0].toIntOrNull()
    val instanceId = args[1].toIntOrNull()

    if (dungeonId == null || instanceId == null) {
        sender.sendMessage("${getString(StringConst.CHAT_PREFIX)}Dungeon id and instance id should both be integers")
        return true
    }

    sender.sendMessage( getString(StringConst.CHAT_PREFIX) +
            if (FWDungeonsController.evacuateDungeon(dungeonId, instanceId))
                "All adventurers were brought back to safety and the instance was reset"
            else
                "Dungeon instance not found")

    return true
}

fun cmdDungeonPlayerLookup(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
    if (args.count() == 0) {
        sender.sendMessage("${getString(StringConst.CHAT_PREFIX)}Please provide a player name")
        return true
    }

    sender.sendMessage(FWDungeonsController.lookupPlayer(args[0]))
    return true
}

fun cmdDungeonEnable(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
    if (args.count() < 1) {
        sender.sendMessage("${getString(StringConst.CHAT_PREFIX)}Please provide a dungeon id")
        return true
    }

    val dungeonId = args[0].toIntOrNull()

    if (dungeonId == null) {
        sender.sendMessage("${getString(StringConst.CHAT_PREFIX)}Dungeon id should be an integer")
        return true
    }

    sender.sendMessage( getString(StringConst.CHAT_PREFIX) +
            if (FWDungeonsController.playerEnableDungeon(dungeonId))
                "Dungeon (id: $dungeonId) was enabled"
            else
                "No dungeon found with id $dungeonId")

    return true
}

fun cmdDungeonDisable(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
    if (args.count() < 1) {
        sender.sendMessage("${getString(StringConst.CHAT_PREFIX)}Please provide a dungeon id")
        return true
    }

    val dungeonId = args[0].toIntOrNull()

    if (dungeonId == null) {
        sender.sendMessage("${getString(StringConst.CHAT_PREFIX)}Dungeon id should be an integer")
        return true
    }

    sender.sendMessage( getString(StringConst.CHAT_PREFIX) +
            if (FWDungeonsController.playerDisableDungeon(dungeonId))
                "Dungeon (id: $dungeonId) was disabled"
            else
                "No dungeon found with id $dungeonId")

    return true
}