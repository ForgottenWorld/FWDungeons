@file:Suppress("unused")

package it.forgottenworld.dungeons.core.utils

import com.okkero.skedule.BukkitDispatcher
import it.forgottenworld.dungeons.core.FWDungeonsPlugin
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

fun launch(f: suspend CoroutineScope.() -> Unit) = CoroutineScope(Dispatchers.minecraft).launch(block = f)

fun launchAsync(f: suspend CoroutineScope.() -> Unit) = CoroutineScope(Dispatchers.async).launch(block = f)

val Dispatchers.minecraft: CoroutineContext get() = BukkitDispatcher(FWDungeonsPlugin.getInstance())

val Dispatchers.async: CoroutineContext get() = BukkitDispatcher(FWDungeonsPlugin.getInstance(), true)