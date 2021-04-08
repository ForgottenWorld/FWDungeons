package it.forgottenworld.dungeons.core.utils

import it.forgottenworld.dungeons.core.storage.Strings
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.format.TextColor
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender

inline fun CommandSender.sendJsonMessage(build: BaseComponentBuilderScope.() -> Unit) {
    (this as Audience).sendMessage(jsonMessage(build))
}

fun CommandSender.sendJsonMessage(component: Component) {
    (this as Audience).sendMessage(component)
}

inline fun jsonMessage(build: BaseComponentBuilderScope.() -> Unit): Component {
    val scope = BaseComponentBuilderScope()
    scope.build()
    return scope.createComponent()
}

class BaseComponentBuilderScope {
    private var component = Component.empty()

    operator fun ClickEvent.unaryPlus() {
        component = component.clickEvent(this)
    }

    operator fun TextColor.unaryPlus() {
        component = component.color(this)
    }

    operator fun String.unaryPlus() {
        component = component.append(Component.text(this))
    }

    operator fun Component.unaryPlus() {
        component = component.append(this)
    }

    fun createComponent() = component
}

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

fun CommandSender.sendPrefixedMessage(message: String) {
    (this as Audience).sendPrefixedMessage(message)
}

fun CommandSender.sendPrefixedMessage(message: String, vararg params: Any?) {
    (this as Audience).sendPrefixedMessage(message, *params)
}

fun Collection<CommandSender>.sendPrefixedMessage(message: String) {
    Audience.audience(map { it as Audience }).sendPrefixedMessage(message)
}

fun sendConsoleMessage(message: String) {
    Bukkit.getServer().consoleSender.sendMessage(message)
}