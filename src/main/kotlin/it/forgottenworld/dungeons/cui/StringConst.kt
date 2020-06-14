package it.forgottenworld.dungeons.cui

import net.md_5.bungee.api.ChatColor

fun getString(const: StringConst) = const.string

enum class StringConst(val string: String) {
    CHAT_PREFIX("${ChatColor.DARK_RED}F${ChatColor.GOLD}W${ChatColor.YELLOW}D${ChatColor.WHITE} ")
}