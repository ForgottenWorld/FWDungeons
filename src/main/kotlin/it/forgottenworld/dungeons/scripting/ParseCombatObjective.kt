package it.forgottenworld.dungeons.scripting

import it.forgottenworld.dungeons.model.DungeonInstance
import it.forgottenworld.dungeons.model.MobSpawnData
import it.forgottenworld.dungeons.manager.InstanceObjectiveManager

fun parseCombatObjective(
        codeIterator: Iterator<String>): (DungeonInstance) -> Unit {
    var currentActiveArea: Int? = null
    val mobs = mutableListOf<MobSpawnData>()
    while(codeIterator.hasNext()) {
        val code = codeIterator.next()
        when {
            code.startsWith(PREFIX_MYTHIC_MOB) -> {
                if (currentActiveArea == null) throw Exception("Target active area not yet set")
                mobs.add(MobSpawnData(currentActiveArea, code.removePrefix(PREFIX_MYTHIC_MOB), true))
            }
            code.startsWith(PREFIX_VANILLA_MOB) -> {
                if (currentActiveArea == null) throw Exception("Target active area not yet set")
                mobs.add(MobSpawnData(currentActiveArea, code.removePrefix(PREFIX_VANILLA_MOB), false))
            }
            code.startsWith(PREFIX_ACTIVE_AREA) ->
                currentActiveArea = code.removePrefix(PREFIX_ACTIVE_AREA).toInt()
            code == CODE_WHEN_DONE ->
                return { InstanceObjectiveManager.attachNewObjectiveToInstance(it, mobs, doParseCode(codeIterator)) }
        }
    }
    return {}
}