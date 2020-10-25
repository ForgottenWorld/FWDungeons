package it.forgottenworld.dungeons.command.play.dungeon

import it.forgottenworld.dungeons.cli.Strings
import it.forgottenworld.dungeons.cli.getLockClickable
import it.forgottenworld.dungeons.cli.getString
import it.forgottenworld.dungeons.model.Party
import it.forgottenworld.dungeons.manager.DungeonManager
import it.forgottenworld.dungeons.manager.DungeonManager.party
import it.forgottenworld.dungeons.utils.sendFWDMessage
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

fun cmdDungeonJoinInstance(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
    if (sender !is Player) return true
    if (args.count() < 2) {
        sender.sendFWDMessage("Please provide both a dungeon and instance id")
        return true
    }

    val dungeonId = args[0].toIntOrNull()
    val instanceId = args[1].toIntOrNull()

    if (dungeonId == null || instanceId == null) {
        sender.sendFWDMessage("Dungeon id and instance id should both be integers")
        return true
    }

    val partyKey = if (args.count() > 2) args[2] else ""

    val dungeon = DungeonManager.getDungeonById(dungeonId) ?: run {
        sender.sendFWDMessage("Invalid dungeon id")
        return true
    }
    val instance = dungeon.instances.find { it.id == instanceId } ?: run {
        sender.sendFWDMessage("Invalid instance id")
        return true
    }

    if (DungeonManager.activeDungeons[dungeonId] != true) {
        sender.sendFWDMessage("This dugeons is disabled")
        return true
    }

    if (sender.party != null) {
        sender.sendFWDMessage("You're already in a party")
        return true
    }

    val party = instance.party

    if (party == null) {
        instance.party = Party(
                mutableListOf(sender),
                sender,
                dungeon.numberOfPlayers.last,
                false,
                instance
        ).also { sender.party = it }
        sender.spigot().sendMessage(
                TextComponent("${getString(Strings.CHAT_PREFIX)}Dungeon party created. To make it private, click ")
                        .apply {
                            addExtra(getLockClickable())
                        })
        return true
    }

    if (party.isFull) {
        sender.sendFWDMessage("This dungeon party is full")
        return true
    }

    if (party.inGame) {
        sender.sendFWDMessage("This dungeon party has already entered the dungeon")
        return true
    }

    if (instance.party!!.isLocked && partyKey != instance.party!!.partyKey) {
        sender.sendFWDMessage("This dungeon party is private and you were not invited")
        return true
    }

    if (party.onPlayerJoin(sender))
        sender.sendFWDMessage("You joined the dungeon party")
    else
        sender.sendFWDMessage("Couldn't join dungeon party")

    return true
}