package it.forgottenworld.dungeons.command.play

import it.forgottenworld.dungeons.controller.FWDungeonsController
import it.forgottenworld.dungeons.cui.getInteractiveDungeonList
import it.forgottenworld.dungeons.cui.getLockClickable
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

val dungeonCmdBindings: Map<String, (CommandSender, Command, String, Array<String>) -> Boolean> =
        mapOf(
                "join" to ::cmdDungeonJoinInstance,
                "list" to ::cmdDungeonList,
                "invite" to ::cmdDungeonInvite,
                "leave" to ::cmdDungeonLeave,
                "escape" to ::cmdDungeonEscape,
                "lock" to ::cmdDungeonLockParty,
                "unlock" to ::cmdDungeonUnlockParty,
                "start" to ::cmdDungeonStart
        )

fun cmdDungeonJoinInstance(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
    if (sender is Player) {
        if (args.count() < 2) {
            sender.sendMessage("Please provide both a dungeon and instance id")
            return true
        }

        val dungeonId = args[0].toIntOrNull()
        val instanceId = args[1].toIntOrNull()

        if (dungeonId == null || instanceId == null) {
            sender.sendMessage("Dungeon id and instance id should both be integers")
            return true
        }

        val partyKey = if (args.count() > 2) args[2] else ""
        when (FWDungeonsController.playerJoinInstance(sender, instanceId, dungeonId, partyKey)) {
            -1 -> sender.sendMessage("Invalid dungeon id")
            -2 -> sender.sendMessage("Invalid instance id")
            -3 -> sender.sendMessage("You're already in a party")
            -4 -> sender.sendMessage("This dungeon party is full")
            -5 -> sender.sendMessage("This dungeon party is private and you were not invited")
            -6 -> sender.sendMessage("Couldn't join dungeon party")
            -7 -> sender.sendMessage("This dungeon party has already entered the dungeon")
            0 -> sender.spigot().sendMessage(
                    TextComponent("Dungeon party created. If you want to make it private, click ")
                            .apply {
                                addExtra(getLockClickable())
                            })
            1 -> sender.sendMessage("You joined the dungeon party")
        }
    }
    return true
}

fun cmdDungeonList(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
    if (sender is Player) {
        val page = if (args.count() != 0 && args[0].toIntOrNull() != null) args[0].toInt() else 0
        sender.spigot().sendMessage(getInteractiveDungeonList(sender, page))
    }
    return true
}

fun cmdDungeonInvite(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
    if (sender is Player) {
        if (args.count() == 0) {
            sender.sendMessage("Please provide the name of whomever you want invite")
            return true
        }

        sender.sendMessage( when (FWDungeonsController.playerSendInvite(sender, args[0])) {
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
        sender.sendMessage( when (FWDungeonsController.playerLeaveParty(sender)) {
            -1 -> "You're currently not in a dungeon party"
            0 -> "You left the dungeon party"
            else -> ""
        })
    }
    return true
}

fun cmdDungeonLockParty(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
    if (sender is Player) {
            sender.sendMessage( when (FWDungeonsController.playerLockParty(sender)) {
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
        sender.sendMessage( when (FWDungeonsController.playerUnlockParty(sender)) {
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
        sender.sendMessage( when (FWDungeonsController.playerStart(sender)) {
            -1 -> "You're currently not in a dungeon party"
            -2 -> "Only the dungeon party leader may start the instance"
            -3 -> "Not enough players for this dungeon"
            0 -> "Dungeon party members have been teleported to the dungeon entrance"
            else -> ""
        })
    }
    return true
}

fun cmdDungeonEscape(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
    if (sender is Player) {

    }
    return true
}
