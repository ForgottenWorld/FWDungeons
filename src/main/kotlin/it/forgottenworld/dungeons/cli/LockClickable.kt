package it.forgottenworld.dungeons.cli

import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.TextComponent


fun getLockClickable() =
        TextComponent("HERE").apply {
            color = ChatColor.GOLD
            clickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, "/fwdungeons lock")
        }

fun getUnlockClickable() =
        TextComponent("HERE").apply {
            color = ChatColor.GOLD
            clickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, "/fwdungeons unlock")
        }