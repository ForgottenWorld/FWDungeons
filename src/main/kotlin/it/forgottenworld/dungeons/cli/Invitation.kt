package it.forgottenworld.dungeons.cli

import it.forgottenworld.dungeons.utils.ktx.append
import it.forgottenworld.dungeons.utils.ktx.clickEvent
import it.forgottenworld.dungeons.utils.ktx.component
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.ClickEvent


fun formatInvitation(
        senderName: String,
        dungeonId: Int,
        instanceId: Int,
        partyKey: String
) = component {
    append("${getString(Strings.CHAT_PREFIX)}$senderName invited you to join a dungeon party, click ")
    append("HERE", ChatColor.GREEN)
    clickEvent(ClickEvent.Action.RUN_COMMAND, "/fwdungeons join $dungeonId $instanceId $partyKey")
    append(" to accept")
}