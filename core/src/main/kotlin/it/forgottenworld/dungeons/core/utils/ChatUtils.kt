package it.forgottenworld.dungeons.core.utils

import it.forgottenworld.dungeons.core.config.Strings
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.BaseComponent
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.ComponentBuilder
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

inline fun Player.sendJsonMessage(build: ComponentBuilderScope.() -> Unit) = spigot()
    .sendMessage(*jsonMessage(build))

fun Player.sendJsonMessage(chatComponent: Array<out BaseComponent>) = spigot()
    .sendMessage(*chatComponent)

inline fun jsonMessage(build: ComponentBuilderScope.() -> Unit): Array<BaseComponent> {
    val builder = ComponentBuilder()
    ComponentBuilderScope(builder).build()
    return builder.create()
}

class ComponentBuilderScope(
    private val componentBuilder: ComponentBuilder
) {
    operator fun ClickEvent.unaryPlus() {
        componentBuilder.event(this)
    }

    operator fun ChatColor.unaryPlus() {
        componentBuilder.color(this)
    }

    operator fun String.unaryPlus() {
        componentBuilder.append(this)
    }

    operator fun Array<out BaseComponent>.unaryPlus() {
        componentBuilder.append(this)
    }
}

fun CommandSender.sendPrefixedMessage(message: String) = sendMessage("${Strings.CHAT_PREFIX}$message")