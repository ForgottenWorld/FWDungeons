package it.forgottenworld.dungeons.cli

import it.forgottenworld.dungeons.config.Strings
import it.forgottenworld.dungeons.utils.append
import it.forgottenworld.dungeons.utils.clickEvent
import it.forgottenworld.dungeons.utils.chatComponent
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.ClickEvent


fun formatInvitation(
    senderName: String,
    dungeonId: Int,
    instanceId: Int,
    partyKey: String
) = chatComponent {
    append("${Strings.CHAT_PREFIX}${Strings.PLAYER_INVITED_YOU_TO_JOIN_PARTY_CLICK.format(senderName)} ")
    append(Strings.HERE, ChatColor.GREEN)
    clickEvent(ClickEvent.Action.RUN_COMMAND, "/fwdungeons join $dungeonId $instanceId $partyKey")
    append(" ${Strings.TO_ACCEPT}", ChatColor.WHITE)
}