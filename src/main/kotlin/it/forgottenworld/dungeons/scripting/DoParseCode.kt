package it.forgottenworld.dungeons.scripting

import it.forgottenworld.dungeons.config.Strings
import it.forgottenworld.dungeons.model.instance.DungeonFinalInstance
import it.forgottenworld.dungeons.utils.ktx.launch
import it.forgottenworld.dungeons.utils.ktx.sendFWDMessage
import kotlinx.coroutines.delay
import org.bukkit.Material

fun doParseCode(codeIterator: Iterator<String>): (DungeonFinalInstance) -> Unit {
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
                    it.players.forEach { p -> p?.sendFWDMessage(Strings.YOU_WILL_EXIT_THE_DUNGEON_IN_5_SECS) }
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