package it.forgottenworld.dungeons.scripting

import it.forgottenworld.dungeons.model.dungeon.DungeonInstance
import it.forgottenworld.dungeons.state.MobSpawnData
import it.forgottenworld.dungeons.state.MobState
import it.forgottenworld.dungeons.utils.bukkitThreadLater
import it.forgottenworld.dungeons.utils.sendFWDMessage
import net.md_5.bungee.api.ChatColor
import org.bukkit.Material

fun doParseCode(instance: DungeonInstance, codeIterator: Iterator<String>): () -> Unit {
    while(codeIterator.hasNext()) {
        when (codeIterator.next()) {
            CODE_COMBAT_OBJECTIVE -> {
                val mobs = mutableListOf<MobSpawnData>()
                val whenDone = parseCombatObjective(instance, codeIterator, mobs)
                val dungeonId = instance.dungeon.id
                val instanceId = instance.id

                return {
                    MobState.attachNewObjectiveToInstance(
                            dungeonId,
                            instanceId,
                            mobs,
                            whenDone)
                }
            }
            CODE_FILL_ACTIVE_AREA -> {
                val activeArea = instance.getActiveAreaById(
                        codeIterator.next().toInt())!!
                val material = Material.getMaterial(codeIterator.next(), false)!!
                return { activeArea.fillWithMaterial(material) }
            }
            CODE_FINISH ->
                return {
                    instance.party?.players?.forEach {
                        it.sendFWDMessage("${ChatColor.GREEN}You will exit the dungeon in 5 seconds...")
                    }

                    bukkitThreadLater(100) { instance.onInstanceFinish(true) }
                }
            CODE_WHEN_DONE ->
                throw Exception("ERROR: whenDone used outside of spawntobekilled statement")
        }
    }
    return {}
}