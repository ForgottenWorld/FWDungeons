package it.forgottenworld.dungeons.scripting

import it.forgottenworld.dungeons.model.activearea.ActiveArea
import it.forgottenworld.dungeons.model.dungeon.DungeonInstance
import it.forgottenworld.dungeons.state.MobSpawnData
import it.forgottenworld.dungeons.state.MobType

fun parseCombatObjective(
        instance: DungeonInstance,
        codeIterator: Iterator<String>,
        mobs: MutableList<MobSpawnData>): () -> Unit {
    var currentActiveArea: ActiveArea? = null
    while(codeIterator.hasNext()) {
        val code = codeIterator.next()
        when {
            code.startsWith(PREFIX_MYTHIC_MOB) ->
                mobs.add(MobSpawnData(currentActiveArea!!, code.removePrefix(PREFIX_MYTHIC_MOB), MobType.MYTHIC))
            code.startsWith(PREFIX_VANILLA_MOB) ->
                mobs.add(MobSpawnData(currentActiveArea!!, code.removePrefix(PREFIX_VANILLA_MOB), MobType.VANILLA))
            code.startsWith(PREFIX_ACTIVE_AREA) ->
                currentActiveArea = instance.getActiveAreaById(code.removePrefix(PREFIX_ACTIVE_AREA).toInt())
            code == CODE_WHEN_DONE ->
                return doParseCode(instance, codeIterator)
        }
    }
    return {}
}