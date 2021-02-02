package it.forgottenworld.dungeons.utils

import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.BaseComponent
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.ComponentBuilder
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.hover.content.Content


inline fun chatComponent(build: ComponentBuilder.() -> Unit): Array<BaseComponent> = ComponentBuilder().apply { build() }.create()

fun ComponentBuilder.clickEvent(action: ClickEvent.Action, value: String): ComponentBuilder = event(ClickEvent(action, value))

fun ComponentBuilder.hoverEvent(action: HoverEvent.Action, vararg content: Content): ComponentBuilder = event(HoverEvent(action, *content))

fun ComponentBuilder.hoverEvent(action: HoverEvent.Action, content: Iterable<Content>): ComponentBuilder = event(HoverEvent(action, content.toList()))

fun ComponentBuilder.append(text: String, color: ChatColor): ComponentBuilder = append(text).color(color)