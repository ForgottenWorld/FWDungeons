@file:Suppress("unused")

package it.forgottenworld.dungeons.core.utils

import it.forgottenworld.dungeons.core.config.Strings
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.BaseComponent
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.ComponentBuilder
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.hover.content.Content
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

inline fun chatComponent(build: ComponentBuilder.() -> Unit): Array<BaseComponent> = ComponentBuilder()
    .apply(build)
    .create()

inline fun Player.sendJsonMessage(build: ComponentBuilder.() -> Unit) = spigot()
    .sendMessage(*chatComponent(build))

fun Player.sendJsonMessage(chatComponent: Array<BaseComponent>) = spigot()
    .sendMessage(*chatComponent)

fun CommandSender.sendPrefixedMessage(message: String) = sendMessage("${Strings.CHAT_PREFIX}$message")

fun ComponentBuilder.clickEvent(
    action: ClickEvent.Action,
    value: String
): ComponentBuilder = event(ClickEvent(action, value))

fun ComponentBuilder.hoverEvent(
    action: HoverEvent.Action,
    vararg content: Content
): ComponentBuilder = event(HoverEvent(action, *content))

fun ComponentBuilder.hoverEvent(
    action: HoverEvent.Action,
    content: Iterable<Content>
): ComponentBuilder = event(HoverEvent(action, content.toList()))

fun ComponentBuilder.append(
    text: String,
    color: ChatColor
): ComponentBuilder = append(text).color(color)