@file:Suppress("unused")

package it.forgottenworld.dungeons.utils.ktx

import com.github.shynixn.mccoroutine.asyncDispatcher
import com.github.shynixn.mccoroutine.launch
import com.github.shynixn.mccoroutine.launchAsync
import com.github.shynixn.mccoroutine.minecraftDispatcher
import it.forgottenworld.dungeons.FWDungeonsPlugin
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.bukkit.plugin.java.JavaPlugin
import kotlin.coroutines.CoroutineContext

fun launch(f: suspend CoroutineScope.() -> Unit) {
    JavaPlugin.getPlugin(FWDungeonsPlugin::class.java).launch(f)
}

fun launchAsync(f: suspend CoroutineScope.() -> Unit) {
    JavaPlugin.getPlugin(FWDungeonsPlugin::class.java).launchAsync(f)
}

val Dispatchers.minecraft: CoroutineContext
    get() = JavaPlugin.getPlugin(FWDungeonsPlugin::class.java).minecraftDispatcher

val Dispatchers.async: CoroutineContext
    get() = JavaPlugin.getPlugin(FWDungeonsPlugin::class.java).asyncDispatcher