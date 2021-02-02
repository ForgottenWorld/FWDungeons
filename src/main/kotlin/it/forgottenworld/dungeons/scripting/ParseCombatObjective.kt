package it.forgottenworld.dungeons.scripting

import it.forgottenworld.dungeons.model.instance.DungeonFinalInstance
import it.forgottenworld.dungeons.utils.MobSpawnData

fun parseCombatObjective(
    codeIterator: Iterator<String>): (DungeonFinalInstance) -> Unit {
    var currentActiveArea: Int? = null
    val mobs = mutableListOf<MobSpawnData>()
    while (codeIterator.hasNext()) {
        val code = codeIterator.next()
        when {
            code.startsWith(PREFIX_MYTHIC_MOB) -> {
                if (currentActiveArea == null) throw ScriptingException("Target active area not yet set")
                mobs.add(MobSpawnData(currentActiveArea, code.removePrefix(PREFIX_MYTHIC_MOB), true))
            }
            code.startsWith(PREFIX_VANILLA_MOB) -> {
                if (currentActiveArea == null) throw ScriptingException("Target active area not yet set")
                mobs.add(MobSpawnData(currentActiveArea, code.removePrefix(PREFIX_VANILLA_MOB), false))
            }
            code.startsWith(PREFIX_ACTIVE_AREA) ->
                currentActiveArea = code.removePrefix(PREFIX_ACTIVE_AREA).toInt()
            code == CODE_WHEN_DONE -> {
                val whenDone = parseTokens(codeIterator)
                return { it.attachNewObjective(mobs, whenDone) }
            }
            else -> code.toIntOrNull()?.let { for (i in 1 until it) mobs.add(mobs.last().clone()) }
        }
    }
    return {}
}