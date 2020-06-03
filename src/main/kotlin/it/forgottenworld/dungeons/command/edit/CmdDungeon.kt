package it.forgottenworld.dungeons.command.edit

import it.forgottenworld.dungeons.controller.FWDungeonsEditController
import it.forgottenworld.dungeons.utils.getTargetBlock
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

val dungeonCmdBindings: Map<String, (CommandSender, Command, String, Array<String>) -> Boolean> =
        mapOf(
                "create" to ::cmdDungeonCreate,
                "edit" to ::cmdDungeonEdit,
                "name" to ::cmdDungeonName,
                "pos1" to ::cmdDungeonPos1,
                "pos2" to ::cmdDungeonPos2,
                "instadd" to ::cmdDungeonInstanceAdd,
                "instremove" to ::cmdDungeonInstanceRemove,
                "writeout" to ::cmdDungeonWriteOut,
                "setstart" to ::cmdDungeonSetStart
        )

fun cmdDungeonCreate(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
    if (sender is Player) {
        val id = FWDungeonsEditController.playerCreateDungeon(sender)
        sender.sendMessage("Created dungeon with id $id")
    }
    return true
}

fun cmdDungeonEdit(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
    if (sender is Player) {
        if (args.count() == 0) {
            sender.sendMessage("Not enough arguments: please provide a dungeon id")
            return true
        }

        val id = args[0].toIntOrNull()
        if (id == null) {
            sender.sendMessage("Dungeon id should be an integer")
            return true
        }

        if (FWDungeonsEditController.playerEditDungeon(sender, id))
            sender.sendMessage("Now editing dungeon with id $id")
        else
            sender.sendMessage("No dungeons found with id $id")
    }
    return true
}

fun cmdDungeonPos1(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
    if (sender is Player) {
        val block = sender.getTargetBlock()

        if (block == null) {
            sender.sendMessage("You need to be targeting a block within 5 blocks of you before calling this")
            return true
        }

        sender.sendMessage(
                when (FWDungeonsEditController.playerSetDungeonPos1(sender, block)) {
                    0 -> "Dungeon box set"
                    -1 -> "You're not editing any dungeons"
                    -2 -> "First position set, now pick another with /fwde dungeon pos2"
                    else -> ""
                }
        )
    }
    return true
}

fun cmdDungeonPos2(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
    if (sender is Player) {
        val block = sender.getTargetBlock()

        if (block == null) {
            sender.sendMessage("You need to be targeting a block within 5 blocks of you before calling this")
            return true
        }

        sender.sendMessage(
                when (FWDungeonsEditController.playerSetDungeonPos2(sender, block)) {
                    0 -> "Dungeon box set"
                    -1 -> "You're not editing any dungeons"
                    -2 -> "First position set, now pick another with /fwde dungeon pos1"
                    else -> ""
                }
        )
    }
    return true
}

fun cmdDungeonInstanceAdd(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
    if (sender is Player) {
        val block = sender.getTargetBlock()

        if (block == null) {
            sender.sendMessage("You need to be targeting a block within 5 blocks of you before calling this")
            return true
        }

        val ret = FWDungeonsEditController.playerAddInstance(sender, block)
        sender.sendMessage(
                when (ret) {
                    -1 -> "You're not editing any dungeons"
                    -2 -> "Dungeon instances may only be created for fully protoyped dungeons"
                    else -> "Created instance with id $ret"
                }
        )
    }

    return true
}

fun cmdDungeonInstanceRemove(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
    if (sender is Player) {
        val ret = FWDungeonsEditController.playerRemoveInstance(sender)
        sender.sendMessage(
                when (ret) {
                    -1 -> "You're not editing any dungeons"
                    -2 -> "Dungeon instances may only be removed from fully protoyped dungeons"
                    -3 -> "Dungeon has no instances"
                    -4 -> "No instances at this location"
                    else -> "Removed instance with id $ret"
                }
        )
    }

    return true
}

fun cmdDungeonName(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
    if (sender is Player) {
        if (args.count() == 0) {
            sender.sendMessage("Not enough arguments: please provide a name")
        }
        sender.sendMessage(
                when (FWDungeonsEditController.playerNameDungeon(sender, args.joinToString(" "))) {
                    0 -> "Dungeon name changed"
                    -1 -> "You're not editing any dungeons"
                    -2 -> "Antoher dungeon with the same name already exists"
                    -3 -> "Antoher dungeon with the same name is being created by someone"
                    else -> ""
                }
        )
    }

    return true
}

fun cmdDungeonWriteOut(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
    if (sender is Player) {
        sender.sendMessage(
                 FWDungeonsEditController.playerWriteOutDungeon(sender)
        )
    }

    return true
}

fun cmdDungeonSetStart(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
    if (sender is Player) {
        sender.sendMessage(
                when (FWDungeonsEditController.playerSetStartDungeon(sender)) {
                    0 -> "Dungeon starting location set succesfully"
                    -1 -> "You're not editing any dungeons"
                    -2 -> "Dungeon box should be set before adding a starting location"
                    -3 -> "You're outside of the dungeon box"
                    else -> ""
                })
    }

    return true
}