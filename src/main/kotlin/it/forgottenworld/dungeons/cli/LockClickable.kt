package it.forgottenworld.dungeons.cli

import it.forgottenworld.dungeons.utils.clickEvent
import it.forgottenworld.dungeons.utils.component
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.ClickEvent


fun getLockClickable() = component {
    append("HERE")
    color(ChatColor.GOLD)
    clickEvent(ClickEvent.Action.RUN_COMMAND, "/fwdungeons lock")
}

fun getUnlockClickable() = component {
    append("HERE")
    color(ChatColor.RED)
    clickEvent(ClickEvent.Action.RUN_COMMAND, "/fwdungeons unlock")
}