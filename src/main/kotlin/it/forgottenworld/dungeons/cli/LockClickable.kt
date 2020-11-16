package it.forgottenworld.dungeons.cli

import it.forgottenworld.dungeons.config.Strings
import it.forgottenworld.dungeons.utils.ktx.clickEvent
import it.forgottenworld.dungeons.utils.ktx.component
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.ClickEvent


fun getLockClickable() = component {
    append(Strings.HERE)
    color(ChatColor.GOLD)
    clickEvent(ClickEvent.Action.RUN_COMMAND, "/fwdungeons lock")
}

fun getUnlockClickable() = component {
    append(Strings.HERE)
    color(ChatColor.GREEN)
    clickEvent(ClickEvent.Action.RUN_COMMAND, "/fwdungeons unlock")
}