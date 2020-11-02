package it.forgottenworld.dungeons.scripting

import it.forgottenworld.dungeons.model.instance.DungeonFinalInstance
import it.forgottenworld.dungeons.utils.RewindableIterator
import it.forgottenworld.dungeons.utils.ktx.sendFWDMessage
import it.forgottenworld.dungeons.utils.launch
import kotlinx.coroutines.delay
import net.md_5.bungee.api.ChatColor
import org.bukkit.Material

fun doParseCode(codeIterator: RewindableIterator<String>): (DungeonFinalInstance) -> Unit {
    while(codeIterator.hasNext()) {
        when (val code = codeIterator.next()) {
            CODE_COMBAT_OBJECTIVE -> {
                val combatObjective = parseCombatObjective(codeIterator)
                return { combatObjective(it) }
            }
            CODE_FILL_ACTIVE_AREA -> {
                val aaId = codeIterator.next().toInt()
                val material = Material.getMaterial(codeIterator.next(), false)!!
                return { (it.activeAreas[aaId] ?: error("")).fillWithMaterial(material) }
            }
            CODE_FINISH ->
                return {
                    it.players.forEach { p -> p?.sendFWDMessage("${ChatColor.GREEN}You will exit the dungeon in 5 seconds...") }
                    launch {
                        delay(5000)
                        it.onInstanceFinish(true)
                    }
                }
            CODE_WHEN_DONE ->
                throw Exception("ERROR: whenDone used outside of combatObjective statement")
            else -> throw Exception("ERROR: unrecognized code $code")
        }
    }
    return {}
}