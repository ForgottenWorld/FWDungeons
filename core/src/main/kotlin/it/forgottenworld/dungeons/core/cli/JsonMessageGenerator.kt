package it.forgottenworld.dungeons.core.cli

import com.google.inject.Singleton
import it.forgottenworld.dungeons.core.storage.Strings
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.format.NamedTextColor

@Singleton
class JsonMessageGenerator {

    fun invitation(
        senderName: String,
        dungeonId: Int,
        instanceId: Int,
        partyKey: String
    ) = TextComponent.ofChildren(
        Component.text(Strings.CHAT_PREFIX),
        Component.text(
            Strings.PLAYER_INVITED_YOU_TO_JOIN_PARTY_CLICK.format(senderName),
            NamedTextColor.WHITE
        ),
        Component.text(Strings.HERE, NamedTextColor.GREEN)
            .clickEvent(ClickEvent.runCommand("/fwdungeons join $dungeonId $instanceId $partyKey")),
        Component.text(Strings.TO_ACCEPT, NamedTextColor.WHITE)
    )

    val lockLink by lazy {
        Component.text(Strings.HERE, NamedTextColor.GOLD)
            .clickEvent(ClickEvent.runCommand("/fwdungeons lock"))
    }

    val unlockLink by lazy {
        Component.text(Strings.HERE, NamedTextColor.GREEN)
            .clickEvent(ClickEvent.runCommand("/fwdungeons unlock"))
    }

}