package it.forgottenworld.dungeons.command.edit.trigger

import it.forgottenworld.dungeons.config.Strings
import it.forgottenworld.dungeons.model.dungeon.EditableDungeon.Companion.editableDungeon
import it.forgottenworld.dungeons.scripting.*
import it.forgottenworld.dungeons.utils.ktx.sendFWDMessage
import net.md_5.bungee.api.ChatColor
import org.bukkit.entity.Player

fun cmdTriggerCode(sender: Player, args: Array<out String>): Boolean {
    val dungeon = sender.editableDungeon ?: run {
        sender.sendFWDMessage(Strings.NOT_EDITING_ANY_DUNGEONS)
        return true
    }

    val triggerId = args.getOrNull(0)?.toIntOrNull() ?: run {
        sender.sendFWDMessage(Strings.PROVIDE_VALID_TRIGGER_ID)
        return true
    }

    val trigger = dungeon.triggers[triggerId] ?: run {
        sender.sendFWDMessage(Strings.TRIGGER_NOT_FOUND)
        return true
    }

    val code = trigger.effectCode.joinToString(";\n") {
        it.replace(CODE_FILL_ACTIVE_AREA, "${ChatColor.of("#bfff00")}$CODE_FILL_ACTIVE_AREA${ChatColor.WHITE}")
                .replace(CODE_COMBAT_OBJECTIVE, "${ChatColor.AQUA}$CODE_COMBAT_OBJECTIVE${ChatColor.WHITE}")
                .replace(CODE_WHEN_DONE, "${ChatColor.LIGHT_PURPLE}$CODE_WHEN_DONE${ChatColor.WHITE}")
                .replace(CODE_FINISH, "${ChatColor.RED}$CODE_FINISH${ChatColor.WHITE}")
                .replace(PREFIX_ACTIVE_AREA, "${ChatColor.GREEN}$PREFIX_ACTIVE_AREA${ChatColor.WHITE}")
                .replace(PREFIX_MYTHIC_MOB, "${ChatColor.of("#ffa500")}$PREFIX_MYTHIC_MOB${ChatColor.WHITE}")
                .replace(PREFIX_VANILLA_MOB, "${ChatColor.GRAY}$PREFIX_VANILLA_MOB${ChatColor.WHITE}")
                .trim()
    }

    sender.sendFWDMessage("\n§7[ CODE ]")
    sender.sendMessage(code)

    return true
}