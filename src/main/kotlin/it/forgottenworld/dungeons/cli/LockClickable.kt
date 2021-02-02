package it.forgottenworld.dungeons.cli

import it.forgottenworld.dungeons.config.Strings
import it.forgottenworld.dungeons.utils.clickEvent
import it.forgottenworld.dungeons.utils.chatComponent
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.ClickEvent


fun getLockClickable() = chatComponent {
    append(Strings.HERE)
    color(ChatColor.GOLD)
    clickEvent(ClickEvent.Action.RUN_COMMAND, "/fwdungeons lock")
}

fun getUnlockClickable() = chatComponent {
    append(Strings.HERE)
    color(ChatColor.GREEN)
    clickEvent(ClickEvent.Action.RUN_COMMAND, "/fwdungeons unlock")
}