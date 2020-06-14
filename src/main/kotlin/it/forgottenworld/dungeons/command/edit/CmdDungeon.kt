package it.forgottenworld.dungeons.command.edit

import it.forgottenworld.dungeons.controller.FWDungeonsEditController
import it.forgottenworld.dungeons.cui.StringConst
import it.forgottenworld.dungeons.cui.getString
import it.forgottenworld.dungeons.model.dungeon.Dungeon
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
                "setstart" to ::cmdDungeonSetStart,
                "discard" to ::cmdDungeonDiscard,
                "difficulty" to ::cmdDungeonDifficulty,
                "description" to ::cmdDungeonDescription,
                "players" to ::cmdDungeonNumberOfPlayers,
                "save" to ::cmdDungeonSave,
                "points" to ::cmdDungeonPoints
        )

fun cmdDungeonCreate(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
    if (sender is Player) {
        val id = FWDungeonsEditController.playerCreateDungeon(sender)
        sender.sendMessage("${getString(StringConst.CHAT_PREFIX)}Created dungeon with id $id")
    }
    return true
}

fun cmdDungeonEdit(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
    if (sender is Player) {
        if (args.count() == 0) {
            sender.sendMessage("${getString(StringConst.CHAT_PREFIX)}Not enough arguments: please provide a dungeon id")
            return true
        }

        val id = args[0].toIntOrNull()
        if (id == null) {
            sender.sendMessage("${getString(StringConst.CHAT_PREFIX)}Dungeon id should be an integer")
            return true
        }

        if (FWDungeonsEditController.playerEditDungeon(sender, id))
            sender.sendMessage("${getString(StringConst.CHAT_PREFIX)}Now editing dungeon with id $id")
        else
            sender.sendMessage("${getString(StringConst.CHAT_PREFIX)}No dungeons found with id $id")
    }
    return true
}

fun cmdDungeonPos1(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
    if (sender is Player) {
        val block = sender.getTargetBlock()

        if (block == null) {
            sender.sendMessage("${getString(StringConst.CHAT_PREFIX)}You need to be targeting a block within 5 blocks of you before calling this")
            return true
        }

        sender.sendMessage( getString(StringConst.CHAT_PREFIX) +
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
            sender.sendMessage("${getString(StringConst.CHAT_PREFIX)}You need to be targeting a block within 5 blocks of you before calling this")
            return true
        }

        sender.sendMessage( getString(StringConst.CHAT_PREFIX) +
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
            sender.sendMessage("${getString(StringConst.CHAT_PREFIX)}You need to be targeting a block within 5 blocks of you before calling this")
            return true
        }

        val ret = FWDungeonsEditController.playerAddInstance(sender, block)
        sender.sendMessage( getString(StringConst.CHAT_PREFIX) +
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
        sender.sendMessage( getString(StringConst.CHAT_PREFIX) +
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
            sender.sendMessage("${getString(StringConst.CHAT_PREFIX)}Not enough arguments: please provide a name")
            return true
        }
        sender.sendMessage( getString(StringConst.CHAT_PREFIX) +
                when (FWDungeonsEditController.playerSetNameDungeon(sender, args.joinToString(" "))) {
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

fun cmdDungeonDescription(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
    if (sender is Player) {
        if (args.count() == 0) {
            sender.sendMessage("${getString(StringConst.CHAT_PREFIX)}Not enough arguments: please provide a description")
            return true
        }
        sender.sendMessage( getString(StringConst.CHAT_PREFIX) +
                when (FWDungeonsEditController.playerSetDescriptionDungeon(sender, args.joinToString(" "))) {
                    0 -> "Dungeon description changed"
                    -1 -> "You're not editing any dungeons"
                    else -> ""
                }
        )
    }

    return true
}

fun cmdDungeonDifficulty(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
    if (sender is Player) {
        if (args.count() == 0) {
            sender.sendMessage("${getString(StringConst.CHAT_PREFIX)}Not enough arguments: please provide a difficulty")
            return true
        }

        Dungeon.Difficulty.values().map { it.toString() }.let {
            if (!it.contains(args[0].toLowerCase())) {
                sender.sendMessage("${getString(StringConst.CHAT_PREFIX)}Invalid argument, possible arguments: ${it.joinToString(", ")}")
                return true
            }

            sender.sendMessage( getString(StringConst.CHAT_PREFIX) +
                    when (FWDungeonsEditController.playerSetDifficultyDungeon(sender, Dungeon.Difficulty.fromString(args[0].toLowerCase())!!)) {
                        0 -> "Dungeon difficulty changed"
                        -1 -> "You're not editing any dungeons"
                        else -> ""
                    }
            )
        }
    }

    return true
}

fun cmdDungeonPoints(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
    if (sender is Player) {
        if (args.count() == 0) {
            sender.sendMessage("${getString(StringConst.CHAT_PREFIX)}Not enough arguments: please provide an amount")
            return true
        }
        val p = args[0].toIntOrNull()
        if (p == null) {
            sender.sendMessage("${getString(StringConst.CHAT_PREFIX)}Invalid argument: amount of points should be an integer")
            return true
        }
        sender.sendMessage( getString(StringConst.CHAT_PREFIX) +
                when (FWDungeonsEditController.playerSetPointsDungeon(sender, p)) {
                    0 -> "Dungeon points changed"
                    -1 -> "You're not editing any dungeons"
                    else -> ""
                }
        )
    }

    return true
}

fun cmdDungeonNumberOfPlayers(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
    if (sender is Player) {
        if (args.count() < 2) {
            sender.sendMessage("${getString(StringConst.CHAT_PREFIX)}Not enough arguments: please provide minimum and maximum players")
            return true
        }

        val r1 = args[0].toIntOrNull()
        val r2 = args[1].toIntOrNull()
        if (r1 == null || r2 == null) {
            sender.sendMessage("${getString(StringConst.CHAT_PREFIX)}Please minimum and maximum players as integers")
            return true
        } else {
            sender.sendMessage( getString(StringConst.CHAT_PREFIX) +
                    when (FWDungeonsEditController.playerSetNumberOfPlayersDungeon(sender, IntRange(r1, r2))) {
                        0 -> "Dungeon number of players changed"
                        -1 -> "You're not editing any dungeons"
                        else -> ""
                    }
            )
        }
    }

    return true
}

fun cmdDungeonWriteOut(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
    if (sender is Player) {
        sender.sendMessage( getString(StringConst.CHAT_PREFIX) +
                 FWDungeonsEditController.playerWriteOutDungeon(sender)
        )
    }

    return true
}

fun cmdDungeonSetStart(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
    if (sender is Player) {
        sender.sendMessage( getString(StringConst.CHAT_PREFIX) +
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

fun cmdDungeonSave(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
    if (sender is Player) {
        sender.sendMessage( getString(StringConst.CHAT_PREFIX) +
                when (FWDungeonsEditController.playerSaveDungeon(sender)) {
                    0 -> "Dungeon saved succesfully"
                    -1 -> "You're not editing any dungeons"
                    -2 -> "This dungeon was never exported, use /fwde dungeon writeout"
                    else -> ""
                })
    }

    return true
}

fun cmdDungeonDiscard(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
    if (sender is Player) {
        sender.sendMessage( getString(StringConst.CHAT_PREFIX) +
                when (FWDungeonsEditController.playerDiscardDungeon(sender)) {
                    0 -> "Dungeon discarded"
                    -1 -> "You're not editing any dungeons"
                    else -> ""
                })
    }

    return true
}