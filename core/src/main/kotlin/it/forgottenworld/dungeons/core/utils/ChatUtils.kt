package it.forgottenworld.dungeons.core.utils

import it.forgottenworld.dungeons.core.storage.Strings
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import org.bukkit.Bukkit

fun Audience.sendPrefixedMessage(message: String) {
    sendMessage(
        TextComponent.ofChildren(
            Component.text(Strings.CHAT_PREFIX),
            Component.text(message)
        )
    )
}

fun Audience.sendPrefixedMessage(message: String, vararg params: Any?) {
    sendMessage(
        TextComponent.ofChildren(
            Component.text(Strings.CHAT_PREFIX),
            Component.text(message.format(*params))
        )
    )
}

fun Iterable<Audience>.sendPrefixedMessage(message: String) {
    Audience.audience(this).sendPrefixedMessage(message)
}

fun sendConsoleMessage(message: String) {
    Bukkit.getServer().consoleSender.sendMessage(message)
}