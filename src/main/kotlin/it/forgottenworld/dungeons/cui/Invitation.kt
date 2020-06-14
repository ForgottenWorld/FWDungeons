package it.forgottenworld.dungeons.cui

import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.TextComponent


fun formatInvitation(senderName: String, dungeonId: Int, instanceId: Int, partyKey: String) =
        TextComponent("${getString(StringConst.CHAT_PREFIX)}$senderName invited you to join a dungeon party, click ").apply {
            addExtra(
                    TextComponent("HERE").apply {
                        color = ChatColor.GREEN
                        clickEvent = ClickEvent(
                                ClickEvent.Action.RUN_COMMAND,
                                "/fwdungeons join $dungeonId $instanceId $partyKey")
                    }
            )
            addExtra(" to accept")
        }