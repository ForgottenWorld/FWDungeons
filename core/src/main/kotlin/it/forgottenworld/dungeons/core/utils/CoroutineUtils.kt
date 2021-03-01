@file:Suppress("unused")

package it.forgottenworld.dungeons.core.utils

import com.okkero.skedule.BukkitDispatcher
import it.forgottenworld.dungeons.core.FWDungeonsPlugin
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.bukkit.plugin.java.JavaPlugin
import kotlin.coroutines.CoroutineContext

fun launch(f: suspend CoroutineScope.() -> Unit) = CoroutineScope(Dispatchers.minecraft).launch(block = f)

fun launchAsync(f: suspend CoroutineScope.() -> Unit) = CoroutineScope(Dispatchers.async).launch(block = f)

val Dispatchers.minecraft: CoroutineContext get() = BukkitDispatcher(
    JavaPlugin.getPlugin(FWDungeonsPlugin::class.java)
)

val Dispatchers.async: CoroutineContext get() = BukkitDispatcher(
    JavaPlugin.getPlugin(FWDungeonsPlugin::class.java),
    true
)