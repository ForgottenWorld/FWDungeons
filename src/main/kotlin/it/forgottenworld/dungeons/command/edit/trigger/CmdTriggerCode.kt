package it.forgottenworld.dungeons.command.edit.trigger

import it.forgottenworld.dungeons.model.dungeon.EditableDungeon.Companion.editableDungeon
import it.forgottenworld.dungeons.scripting.*
import it.forgottenworld.dungeons.utils.ktx.sendFWDMessage
import net.md_5.bungee.api.ChatColor
import org.bukkit.entity.Player

fun cmdTriggerCode(sender: Player, args: Array<out String>): Boolean {
    val dungeon = sender.editableDungeon ?: run {
        sender.sendFWDMessage("You're not editing any dungeons")
        return true
    }

    val triggerId = args.getOrNull(0)?.toIntOrNull() ?: run {
        sender.sendFWDMessage("Please provide a valid trigger id")
        return true
    }

    val trigger = dungeon.triggers[triggerId] ?: run {
        sender.sendFWDMessage("Trigger not found")
        return true
    }

    val code = trigger.effectCode.joinToString("\n") {
        it.replace(CODE_FILL_ACTIVE_AREA, "${ChatColor.of("#bfff00")}$CODE_FILL_ACTIVE_AREA${ChatColor.WHITE}")
                .replace(CODE_COMBAT_OBJECTIVE, "${ChatColor.AQUA}$CODE_COMBAT_OBJECTIVE${ChatColor.WHITE}")
                .replace(CODE_WHEN_DONE, "${ChatColor.LIGHT_PURPLE}$CODE_WHEN_DONE${ChatColor.WHITE}")
                .replace(CODE_FINISH, "${ChatColor.RED}$CODE_FINISH${ChatColor.WHITE}")
                .replace(PREFIX_ACTIVE_AREA, "${ChatColor.GREEN}$PREFIX_ACTIVE_AREA${ChatColor.WHITE}")
                .replace(PREFIX_MYTHIC_MOB, "${ChatColor.of("#ffa500")}$PREFIX_MYTHIC_MOB${ChatColor.WHITE}")
                .replace(PREFIX_VANILLA_MOB, "${ChatColor.GRAY}$PREFIX_VANILLA_MOB${ChatColor.WHITE}")
                .trim()
    }

    sender.sendFWDMessage("\n${ChatColor.GRAY}[ CODE ]")
    sender.sendMessage(code)

    return true
}