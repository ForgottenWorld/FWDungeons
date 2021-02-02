package it.forgottenworld.dungeons.scripting

import it.forgottenworld.dungeons.config.Strings
import it.forgottenworld.dungeons.model.instance.DungeonFinalInstance
import it.forgottenworld.dungeons.utils.launch
import it.forgottenworld.dungeons.utils.sendFWDMessage
import kotlinx.coroutines.delay
import org.bukkit.Material

fun parseTokens(codeIterator: Iterator<String>): (DungeonFinalInstance) -> Unit {
    val parsed = mutableListOf<(DungeonFinalInstance) -> Unit>()
    while (codeIterator.hasNext()) {
        when (val code = codeIterator.next()) {
            CODE_COMBAT_OBJECTIVE -> {
                val combatObjective = parseCombatObjective(codeIterator)
                parsed.add { combatObjective(it) }
            }
            CODE_FILL_ACTIVE_AREA -> {
                val aaId = codeIterator.next().toInt()
                val material = Material.getMaterial(codeIterator.next(), false)!!
                parsed.add {
                    (it.activeAreas[aaId] ?: error("Active area with id $aaId not found"))
                        .fillWithMaterial(material)
                }
            }
            CODE_FINISH -> parsed.add {
                it.players.forEach { p -> p?.sendFWDMessage(Strings.YOU_WILL_EXIT_THE_DUNGEON_IN_5_SECS) }
                launch {
                    delay(5000)
                    it.onInstanceFinish(true)
                }
            }
            CODE_BREAK -> return { for (f in parsed) f(it) }
            CODE_WHEN_DONE -> throw ScriptingException("whenDone used outside of combatObjective statement")
            else -> throw ScriptingException("Unrecognized code $code")
        }
    }
    return { for (f in parsed) f(it) }
}

fun parseScript(lines: List<String>) = parseTokens(lines.iterator())