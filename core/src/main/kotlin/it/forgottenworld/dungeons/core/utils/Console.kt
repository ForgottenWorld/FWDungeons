package it.forgottenworld.dungeons.core.utils

import org.bukkit.Bukkit

fun sendConsoleMessage(message: String) = Bukkit.getServer().consoleSender.sendMessage(message)