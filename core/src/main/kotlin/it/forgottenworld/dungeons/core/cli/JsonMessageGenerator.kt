package it.forgottenworld.dungeons.core.cli

import com.google.inject.Singleton
import it.forgottenworld.dungeons.core.storage.Strings
import it.forgottenworld.dungeons.core.utils.jsonMessage
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.format.NamedTextColor

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
        +NamedTextColor.GREEN
        +ClickEvent.runCommand("/fwdungeons join $dungeonId $instanceId $partyKey")
        +" §f${Strings.TO_ACCEPT}"
    }

    val lockLink by lazy {
        jsonMessage {
            +Strings.HERE
            +NamedTextColor.GOLD
            +ClickEvent.runCommand("/fwdungeons lock")
        }
    }

    val unlockLink by lazy {
        jsonMessage {
            +Strings.HERE
            +NamedTextColor.GREEN
            +ClickEvent.runCommand("/fwdungeons unlock")
        }
    }

}