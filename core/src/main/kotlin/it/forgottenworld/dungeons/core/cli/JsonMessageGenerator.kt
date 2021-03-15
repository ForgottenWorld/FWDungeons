package it.forgottenworld.dungeons.core.cli

import com.google.inject.Singleton
import it.forgottenworld.dungeons.core.config.Strings
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
        +"${Strings.CHAT_PREFIX}${Strings.PLAYER_INVITED_YOU_TO_JOIN_PARTY_CLICK.format(senderName)} "
        +Strings.HERE
        +ChatColor.GREEN
        +ClickEvent(
            ClickEvent.Action.RUN_COMMAND,
            "/fwdungeons join $dungeonId $instanceId $partyKey"
        )
        +" §f${Strings.TO_ACCEPT}"
    }

    val lockLink by lazy {
        jsonMessage {
            +Strings.HERE
            +ChatColor.GOLD
            +ClickEvent(ClickEvent.Action.RUN_COMMAND, "/fwdungeons lock")
        }
    }

    val unlockLink by lazy {
        jsonMessage {
            +Strings.HERE
            +ChatColor.GREEN
            +ClickEvent(ClickEvent.Action.RUN_COMMAND, "/fwdungeons unlock")
        }
    }

}