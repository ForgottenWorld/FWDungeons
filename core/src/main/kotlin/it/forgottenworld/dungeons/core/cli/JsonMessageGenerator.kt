package it.forgottenworld.dungeons.core.cli

import com.google.inject.Singleton
import it.forgottenworld.dungeons.core.config.Strings
import it.forgottenworld.dungeons.core.utils.clickEvent
import it.forgottenworld.dungeons.core.utils.color
import it.forgottenworld.dungeons.core.utils.jsonMessage
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.ClickEvent

@Singleton
class JsonMessageGenerator {

    fun invitation(
        senderName: String,
        dungeonId: Int,
        instanceId: Int,
        partyKey: String
    ) = jsonMessage {
        append("${Strings.CHAT_PREFIX}${Strings.PLAYER_INVITED_YOU_TO_JOIN_PARTY_CLICK.format(senderName)} ")
        append(Strings.HERE) color ChatColor.GREEN
        clickEvent(ClickEvent.Action.RUN_COMMAND, "/fwdungeons join $dungeonId $instanceId $partyKey")
        append(" ${Strings.TO_ACCEPT}") color ChatColor.WHITE
    }

    fun lockLink() = jsonMessage {
        append(Strings.HERE)
        color(ChatColor.GOLD)
        clickEvent(ClickEvent.Action.RUN_COMMAND, "/fwdungeons lock")
    }

    fun unlockLink() = jsonMessage {
        append(Strings.HERE)
        color(ChatColor.GREEN)
        clickEvent(ClickEvent.Action.RUN_COMMAND, "/fwdungeons unlock")
    }

}