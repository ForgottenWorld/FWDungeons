package it.forgottenworld.dungeons.cli

import net.md_5.bungee.api.ChatColor

fun getString(const: Strings) = const.string

enum class Strings(val string: String) {
    CHAT_PREFIX("${ChatColor.DARK_RED}F${ChatColor.GOLD}W${ChatColor.YELLOW}D${ChatColor.WHITE} ")
}