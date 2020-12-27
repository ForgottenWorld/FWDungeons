@file:Suppress("unused")

package it.forgottenworld.dungeons.utils.ktx

import com.github.shynixn.mccoroutine.asyncDispatcher
import com.github.shynixn.mccoroutine.launch
import com.github.shynixn.mccoroutine.launchAsync
import com.github.shynixn.mccoroutine.minecraftDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

fun launch(f: suspend CoroutineScope.() -> Unit) = plugin.launch(f)

fun launchAsync(f: suspend CoroutineScope.() -> Unit) = plugin.launchAsync(f)

val Dispatchers.minecraft: CoroutineContext get() = plugin.minecraftDispatcher

val Dispatchers.async: CoroutineContext get() = plugin.asyncDispatcher